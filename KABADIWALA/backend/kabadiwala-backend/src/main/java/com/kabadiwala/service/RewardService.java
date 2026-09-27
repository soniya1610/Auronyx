package com.kabadiwala.service;

import com.kabadiwala.entity.Reward;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.RewardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * RewardService — Module 3 complete implementation.
 * Manages reward catalog with admin CRUD operations.
 */
@Service
public class RewardService {

    private final RewardRepository rewardRepository;

    public RewardService(RewardRepository rewardRepository) {
        this.rewardRepository = rewardRepository;
    }

    @Transactional(readOnly = true)
    public List<Reward> getAllActiveRewards() {
        return rewardRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<Reward> getAllRewards() {
        return rewardRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Reward> getRewardsByType(Reward.RewardType type) {
        return rewardRepository.findByType(type);
    }

    @Transactional(readOnly = true)
    public Reward getRewardById(Long id) {
        return rewardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reward", "id", id));
    }

    @Transactional
    public Reward createReward(Reward reward) {
        reward.setActive(reward.getActive() != null ? reward.getActive() : true);
        return rewardRepository.save(reward);
    }

    @Transactional
    public Reward updateReward(Long id, Reward updated) {
        Reward existing = getRewardById(id);
        if (updated.getTitle()       != null) existing.setTitle(updated.getTitle());
        if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
        if (updated.getType()        != null) existing.setType(updated.getType());
        if (updated.getPointsCost()  != null) existing.setPointsCost(updated.getPointsCost());
        if (updated.getCashValue()   != null) existing.setCashValue(updated.getCashValue());
        if (updated.getStockLimit()  != null) existing.setStockLimit(updated.getStockLimit());
        if (updated.getActive()      != null) existing.setActive(updated.getActive());
        return rewardRepository.save(existing);
    }

    @Transactional
    public Reward toggleActive(Long id) {
        Reward reward = getRewardById(id);
        reward.setActive(!reward.getActive());
        return rewardRepository.save(reward);
    }

    @Transactional
    public void deleteReward(Long id) {
        Reward reward = getRewardById(id);
        rewardRepository.delete(reward);
    }
}
