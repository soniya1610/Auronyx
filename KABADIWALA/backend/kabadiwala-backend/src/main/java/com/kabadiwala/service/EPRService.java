package com.kabadiwala.service;

import com.kabadiwala.entity.EPRRecord;
import com.kabadiwala.entity.Recycler;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.EPRRecordRepository;
import com.kabadiwala.repository.RecyclerRepository;
import com.kabadiwala.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * EPRService — Module 3 stub.
 * Extended Producer Responsibility compliance tracking.
 * Full implementation deferred to Rewards & Ecosystem Module.
 */
@Service
public class EPRService {

    private final EPRRecordRepository eprRecordRepository;
    private final RecyclerRepository recyclerRepository;

    public EPRService(EPRRecordRepository eprRecordRepository,
                       RecyclerRepository recyclerRepository) {
        this.eprRecordRepository = eprRecordRepository;
        this.recyclerRepository = recyclerRepository;
    }

    @Transactional(readOnly = true)
    public List<EPRRecord> getAllEPRRecords() {
        return eprRecordRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<EPRRecord> getMyEPRRecords() {
        Long userId = SecurityUtils.getCurrentUserId();
        Recycler recycler = recyclerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recycler profile", "userId", userId));
        return eprRecordRepository.findByRecyclerId(recycler.getId());
    }

    @Transactional(readOnly = true)
    public EPRRecord getEPRRecordById(Long id) {
        return eprRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EPRRecord", "id", id));
    }

    @Transactional
    public EPRRecord createEPRRecord(EPRRecord record) {
        return eprRecordRepository.save(record);
    }

    @Transactional
    public EPRRecord verifyEPRRecord(Long id) {
        EPRRecord record = getEPRRecordById(id);
        record.setVerified(true);
        return eprRecordRepository.save(record);
    }
}
