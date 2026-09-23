package com.kabadiwala.service;

import com.kabadiwala.entity.Recycler;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.RecyclerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * RecyclerService — Module 2.
 * Manages Recycler profiles. Recyclers process materials via RecyclingService.
 */
@Service
public class RecyclerService {

    private final RecyclerRepository recyclerRepository;

    public RecyclerService(RecyclerRepository recyclerRepository) {
        this.recyclerRepository = recyclerRepository;
    }

    @Transactional(readOnly = true)
    public List<Recycler> getAllRecyclers() {
        return recyclerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Recycler getRecyclerById(Long id) {
        return recyclerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recycler", "id", id));
    }

    @Transactional(readOnly = true)
    public Recycler getRecyclerByUserId(Long userId) {
        return recyclerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recycler profile", "userId", userId));
    }

    @Transactional
    public Recycler updateActiveStatus(Long recyclerId, boolean active) {
        Recycler recycler = getRecyclerById(recyclerId);
        recycler.setActive(active);
        return recyclerRepository.save(recycler);
    }
}
