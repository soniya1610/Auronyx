package com.kabadiwala.service;

import com.kabadiwala.entity.Collector;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.CollectorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * CollectorService — Module 2.
 * Manages Collector profile data. Collector operations (accept/collect) are in CollectorOperationsService.
 */
@Service
public class CollectorService {

    private final CollectorRepository collectorRepository;

    public CollectorService(CollectorRepository collectorRepository) {
        this.collectorRepository = collectorRepository;
    }

    @Transactional(readOnly = true)
    public List<Collector> getAllCollectors() {
        return collectorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Collector getCollectorById(Long id) {
        return collectorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collector", "id", id));
    }

    @Transactional(readOnly = true)
    public Collector getCollectorByUserId(Long userId) {
        return collectorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Collector profile", "userId", userId));
    }

    @Transactional
    public Collector updateServiceArea(Long collectorId, String serviceArea) {
        Collector collector = getCollectorById(collectorId);
        collector.setServiceArea(serviceArea);
        return collectorRepository.save(collector);
    }

    @Transactional
    public Collector updateAvailability(Long collectorId, boolean available) {
        Collector collector = getCollectorById(collectorId);
        collector.setIsAvailable(available);
        return collectorRepository.save(collector);
    }
}
