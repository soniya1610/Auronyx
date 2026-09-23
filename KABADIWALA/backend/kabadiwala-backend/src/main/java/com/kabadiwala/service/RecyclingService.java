package com.kabadiwala.service;

import com.kabadiwala.dto.RecyclingRecordDto;
import com.kabadiwala.dto.RecyclingUpdateRequest;
import com.kabadiwala.entity.Pickup;
import com.kabadiwala.entity.Recycler;
import com.kabadiwala.entity.RecyclingRecord;
import com.kabadiwala.entity.WasteCategory;
import com.kabadiwala.exception.InvalidTransactionException;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.PickupRepository;
import com.kabadiwala.repository.RecyclerRepository;
import com.kabadiwala.repository.RecyclingRecordRepository;
import com.kabadiwala.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecyclingService {

    private final RecyclingRecordRepository recyclingRecordRepository;
    private final RecyclerRepository recyclerRepository;
    private final PickupRepository pickupRepository;

    public RecyclingService(RecyclingRecordRepository recyclingRecordRepository,
                            RecyclerRepository recyclerRepository,
                            PickupRepository pickupRepository) {
        this.recyclingRecordRepository = recyclingRecordRepository;
        this.recyclerRepository = recyclerRepository;
        this.pickupRepository = pickupRepository;
    }

    /**
     * Validates sequential state machine transitions:
     * COLLECTED -> SORTED -> AGGREGATED -> TRANSPORT -> RECEIVED -> PROCESSING -> RECYCLED
     */
    public static void validateTransition(RecyclingRecord.Status current, RecyclingRecord.Status next) {
        boolean valid = switch (current) {
            case COLLECTED -> next == RecyclingRecord.Status.SORTED || next == RecyclingRecord.Status.AGGREGATED;
            case SORTED -> next == RecyclingRecord.Status.AGGREGATED || next == RecyclingRecord.Status.TRANSPORT;
            case AGGREGATED -> next == RecyclingRecord.Status.TRANSPORT || next == RecyclingRecord.Status.RECEIVED;
            case TRANSPORT -> next == RecyclingRecord.Status.RECEIVED;
            case RECEIVED -> next == RecyclingRecord.Status.PROCESSING;
            case PROCESSING -> next == RecyclingRecord.Status.RECYCLED;
            case RECYCLED -> false;
        };

        if (!valid) {
            throw new InvalidTransactionException(
                    "Invalid recycling status transition from " + current + " to " + next);
        }
    }

    public BigDecimal calculateCO2Saved(WasteCategory category, BigDecimal weight) {
        if (weight == null || category == null) return BigDecimal.ZERO;
        String name = category.getName().toUpperCase();
        double factor = 0.50;
        if (name.contains("PLASTIC")) {
            factor = 1.50;
        } else if (name.contains("METAL")) {
            factor = 2.50;
        } else if (name.contains("E-WASTE") || name.contains("ELECTRONIC")) {
            factor = 3.00;
        } else if (name.contains("PAPER") || name.contains("CARDBOARD")) {
            factor = 0.90;
        } else if (name.contains("GLASS")) {
            factor = 0.30;
        }
        return weight.multiply(BigDecimal.valueOf(factor)).setScale(3, RoundingMode.HALF_UP);
    }

    @Transactional(readOnly = true)
    public List<RecyclingRecordDto> getMyRecyclingRecords() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Pickup> userPickups = pickupRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return userPickups.stream()
                .map(p -> recyclingRecordRepository.findByPickupId(p.getId()))
                .filter(java.util.Optional::isPresent)
                .map(opt -> mapToDto(opt.get()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RecyclingRecordDto getRecordById(Long id) {
        RecyclingRecord rr = recyclingRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RecyclingRecord", "id", id));
        return mapToDto(rr);
    }

    @Transactional(readOnly = true)
    public RecyclingRecordDto getRecordByPickupId(Long pickupId) {
        RecyclingRecord rr = recyclingRecordRepository.findByPickupId(pickupId)
                .orElseThrow(() -> new ResourceNotFoundException("RecyclingRecord", "pickupId", pickupId));
        return mapToDto(rr);
    }

    @Transactional(readOnly = true)
    public List<RecyclingRecordDto> getIncomingRecords() {
        return recyclingRecordRepository.findAll().stream()
                .filter(r -> r.getStatus() == RecyclingRecord.Status.TRANSPORT ||
                             (r.getStatus() == RecyclingRecord.Status.AGGREGATED && r.getRecycler() == null))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecyclingRecordDto> getMyRecyclerRecords() {
        Long userId = SecurityUtils.getCurrentUserId();
        Recycler recycler = recyclerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recycler", "userId", userId));
        return recyclingRecordRepository.findByRecyclerId(recycler.getId())
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public RecyclingRecordDto receiveWaste(Long recordId) {
        Long userId = SecurityUtils.getCurrentUserId();
        Recycler recycler = recyclerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recycler", "userId", userId));

        RecyclingRecord record = recyclingRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("RecyclingRecord", "id", recordId));

        validateTransition(record.getStatus(), RecyclingRecord.Status.RECEIVED);

        record.setRecycler(recycler);
        record.setStatus(RecyclingRecord.Status.RECEIVED);
        record.setHandoverInfo((record.getHandoverInfo() != null ? record.getHandoverInfo() + "; " : "") +
                "Received by " + recycler.getBusinessName());
        return mapToDto(recyclingRecordRepository.save(record));
    }

    @Transactional
    public RecyclingRecordDto updateStatus(Long recordId, RecyclingUpdateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        Recycler recycler = recyclerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recycler", "userId", userId));

        RecyclingRecord record = recyclingRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("RecyclingRecord", "id", recordId));

        RecyclingRecord.Status targetStatus;
        try {
            targetStatus = RecyclingRecord.Status.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidTransactionException("Invalid recycling status: " + request.getStatus());
        }

        validateTransition(record.getStatus(), targetStatus);

        record.setStatus(targetStatus);
        if (request.getHandoverInfo() != null && !request.getHandoverInfo().isBlank()) {
            record.setHandoverInfo(request.getHandoverInfo());
        }
        if (request.getProcessingInfo() != null && !request.getProcessingInfo().isBlank()) {
            record.setProcessingInfo(request.getProcessingInfo());
        }

        return mapToDto(recyclingRecordRepository.save(record));
    }

    public RecyclingRecordDto mapToDto(RecyclingRecord r) {
        RecyclingRecordDto dto = new RecyclingRecordDto();
        dto.setId(r.getId());
        if (r.getTransaction() != null) {
            dto.setTransactionId(r.getTransaction().getId());
            dto.setTransactionRef(r.getTransaction().getTransactionId());
        }
        if (r.getPickup() != null) {
            dto.setPickupId(r.getPickup().getId());
        }
        if (r.getWasteCategory() != null) {
            dto.setWasteCategoryId(r.getWasteCategory().getId());
            dto.setWasteCategoryName(r.getWasteCategory().getName());
        }
        if (r.getWasteItem() != null) {
            dto.setWasteItemId(r.getWasteItem().getId());
            dto.setWasteItemName(r.getWasteItem().getName());
        }
        dto.setWeight(r.getWeight());
        if (r.getCollector() != null) {
            dto.setCollectorId(r.getCollector().getId());
            dto.setCollectorName(r.getCollector().getUser().getName());
        }
        if (r.getRecycler() != null) {
            dto.setRecyclerId(r.getRecycler().getId());
            dto.setRecyclerName(r.getRecycler().getBusinessName());
        }
        dto.setStatus(r.getStatus().name());
        dto.setHandoverInfo(r.getHandoverInfo());
        dto.setProcessingInfo(r.getProcessingInfo());
        dto.setCo2SavedKg(calculateCO2Saved(r.getWasteCategory(), r.getWeight()));
        dto.setCreatedAt(r.getCreatedAt());
        dto.setUpdatedAt(r.getUpdatedAt());
        return dto;
    }
}
