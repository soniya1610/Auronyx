package com.kabadiwala.service;

import com.kabadiwala.entity.Reward;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.RewardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * RewardService — Module 3 stub.
 * Manages reward catalog. Full implementation in Rewards & Ecosystem Module.
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
    public Reward getRewardById(Long id) {
        return rewardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reward", "id", id));
    }

    @Transactional
    public Reward createReward(Reward reward) {
        return rewardRepository.save(reward);
    }

    @Transactional
    public Reward toggleActive(Long id) {
        Reward reward = getRewardById(id);
        reward.setActive(!reward.getActive());
        return rewardRepository.save(reward);
    }
}
