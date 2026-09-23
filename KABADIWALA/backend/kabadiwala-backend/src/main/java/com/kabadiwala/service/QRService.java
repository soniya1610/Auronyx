package com.kabadiwala.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.kabadiwala.dto.QRResponseDto;
import com.kabadiwala.dto.TraceabilityDto;
import com.kabadiwala.entity.*;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.RecyclingRecordRepository;
import com.kabadiwala.repository.TraceabilityRecordRepository;
import com.kabadiwala.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class QRService {

    private final TraceabilityRecordRepository traceabilityRecordRepository;
    private final TransactionRepository transactionRepository;
    private final RecyclingRecordRepository recyclingRecordRepository;
    private final RecyclingService recyclingService;
    private final ObjectMapper objectMapper;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public QRService(TraceabilityRecordRepository traceabilityRecordRepository,
                     TransactionRepository transactionRepository,
                     RecyclingRecordRepository recyclingRecordRepository,
                     RecyclingService recyclingService) {
        this.traceabilityRecordRepository = traceabilityRecordRepository;
        this.transactionRepository = transactionRepository;
        this.recyclingRecordRepository = recyclingRecordRepository;
        this.recyclingService = recyclingService;
        this.objectMapper = new ObjectMapper();
    }

    public byte[] generateQRCodeImage(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code image", e);
        }
    }

    public String generateBase64QR(String text, int width, int height) {
        byte[] imageBytes = generateQRCodeImage(text, width, height);
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }

    @Transactional
    public QRResponseDto getOrCreateQRForTransaction(Long transactionId) {
        Transaction txn = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", transactionId));

        Optional<TraceabilityRecord> existing = traceabilityRecordRepository.findAll().stream()
                .filter(tr -> tr.getTransaction() != null && tr.getTransaction().getId().equals(transactionId))
                .findFirst();

        TraceabilityRecord record;
        if (existing.isPresent()) {
            record = existing.get();
        } else {
            String qrCode = "QR-" + txn.getTransactionId();
            String trackingUrl = frontendUrl + "/trace/" + qrCode;

            // Sanitized payload — strictly no PII, passwords, or tokens
            Map<String, Object> payload = new HashMap<>();
            payload.put("qrCode", qrCode);
            payload.put("transactionId", txn.getTransactionId());
            payload.put("category", txn.getCategory().getName());
            payload.put("item", txn.getWasteItem() != null ? txn.getWasteItem().getName() : "General");
            payload.put("weightKg", txn.getActualWeight());
            payload.put("pickupCity", txn.getPickup().getCity());
            payload.put("verifiedAt", txn.getCompletedAt() != null ? txn.getCompletedAt().toString() : "");

            String payloadJson;
            try {
                payloadJson = objectMapper.writeValueAsString(payload);
            } catch (Exception e) {
                payloadJson = "{\"qrCode\":\"" + qrCode + "\"}";
            }

            Optional<RecyclingRecord> rr = recyclingRecordRepository.findByPickupId(txn.getPickup().getId());

            TraceabilityRecord newRecord = new TraceabilityRecord();
            newRecord.setQrCode(qrCode);
            newRecord.setTransaction(txn);
            newRecord.setRecyclingRecord(rr.orElse(null));
            newRecord.setPayloadJson(payloadJson);
            record = traceabilityRecordRepository.save(newRecord);
        }

        String qrBase64 = generateBase64QR(record.getQrCode(), 250, 250);
        String trackingUrl = frontendUrl + "/trace/" + record.getQrCode();
        TraceabilityDto details = buildTraceabilityDto(record, qrBase64, trackingUrl);

        return new QRResponseDto(record.getQrCode(), qrBase64, trackingUrl, details);
    }

    @Transactional(readOnly = true)
    public TraceabilityDto getTraceabilityByCode(String code) {
        TraceabilityRecord record = traceabilityRecordRepository.findByQrCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("TraceabilityRecord", "qrCode", code));

        String qrBase64 = generateBase64QR(record.getQrCode(), 250, 250);
        String trackingUrl = frontendUrl + "/trace/" + record.getQrCode();
        return buildTraceabilityDto(record, qrBase64, trackingUrl);
    }

    @Transactional(readOnly = true)
    public byte[] getQRImageForCode(String code) {
        traceabilityRecordRepository.findByQrCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("TraceabilityRecord", "qrCode", code));
        return generateQRCodeImage(code, 300, 300);
    }

    private TraceabilityDto buildTraceabilityDto(TraceabilityRecord record, String qrBase64, String trackingUrl) {
        Transaction txn = record.getTransaction();
        Pickup pickup = txn != null ? txn.getPickup() : null;
        RecyclingRecord rr = record.getRecyclingRecord();
        if (rr == null && pickup != null) {
            rr = recyclingRecordRepository.findByPickupId(pickup.getId()).orElse(null);
        }

        TraceabilityDto dto = new TraceabilityDto();
        dto.setQrCode(record.getQrCode());
        dto.setQrBase64(qrBase64);
        dto.setTrackingUrl(trackingUrl);

        if (txn != null) {
            dto.setTransactionRef(txn.getTransactionId());
            dto.setCategory(txn.getCategory().getName());
            dto.setItem(txn.getWasteItem() != null ? txn.getWasteItem().getName() : "General");
            dto.setWeight(txn.getActualWeight());
            dto.setVerifiedAt(txn.getCompletedAt());
            if (txn.getCollector() != null) {
                dto.setCollectorName(txn.getCollector().getUser().getName());
            }
            BigDecimal co2 = recyclingService.calculateCO2Saved(txn.getCategory(), txn.getActualWeight());
            dto.setCo2SavedKg(co2);
        }

        if (pickup != null) {
            dto.setPickupId(pickup.getId());
            dto.setCity(pickup.getCity());
            dto.setPickupStatus(pickup.getStatus().name());
        }

        if (rr != null) {
            dto.setRecyclingStatus(rr.getStatus().name());
            dto.setProcessingInfo(rr.getProcessingInfo());
            if (rr.getRecycler() != null) {
                dto.setRecyclerName(rr.getRecycler().getBusinessName());
            }
        } else {
            dto.setRecyclingStatus("COLLECTED");
        }

        return dto;
    }
}
