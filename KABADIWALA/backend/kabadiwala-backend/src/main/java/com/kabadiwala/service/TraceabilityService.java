package com.kabadiwala.service;

import com.kabadiwala.entity.TraceabilityRecord;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.TraceabilityRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * TraceabilityService — Module 2.
 * Provides lookup and management of TraceabilityRecords.
 * QR generation is handled by QRService.
 */
@Service
public class TraceabilityService {

    private final TraceabilityRecordRepository traceabilityRecordRepository;

    public TraceabilityService(TraceabilityRecordRepository traceabilityRecordRepository) {
        this.traceabilityRecordRepository = traceabilityRecordRepository;
    }

    @Transactional(readOnly = true)
    public List<TraceabilityRecord> getAllRecords() {
        return traceabilityRecordRepository.findAll();
    }

    @Transactional(readOnly = true)
    public TraceabilityRecord getById(Long id) {
        return traceabilityRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TraceabilityRecord", "id", id));
    }

    @Transactional(readOnly = true)
    public TraceabilityRecord getByQrCode(String qrCode) {
        return traceabilityRecordRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new ResourceNotFoundException("TraceabilityRecord", "qrCode", qrCode));
    }

    @Transactional(readOnly = true)
    public List<TraceabilityRecord> getByRecyclingRecordId(Long recyclingRecordId) {
        return traceabilityRecordRepository.findByRecyclingRecordId(recyclingRecordId);
    }
}
