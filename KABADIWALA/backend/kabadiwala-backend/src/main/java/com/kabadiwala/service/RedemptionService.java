package com.kabadiwala.service;

import com.kabadiwala.entity.Redemption;
import com.kabadiwala.entity.Reward;
import com.kabadiwala.exception.InvalidTransactionException;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.RedemptionRepository;
import com.kabadiwala.repository.RewardRepository;
import com.kabadiwala.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * RedemptionService — Module 3 stub.
 * Full implementation deferred to Rewards & Ecosystem Module.
 */
@Service
public class RedemptionService {

    private final RedemptionRepository redemptionRepository;
    private final RewardRepository rewardRepository;

    public RedemptionService(RedemptionRepository redemptionRepository,
                              RewardRepository rewardRepository) {
        this.redemptionRepository = redemptionRepository;
        this.rewardRepository = rewardRepository;
    }

    @Transactional(readOnly = true)
    public List<Redemption> getMyRedemptions() {
        Long userId = SecurityUtils.getCurrentUserId();
        return redemptionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Reward> getAvailableRewards() {
        return rewardRepository.findByActiveTrue();
    }
}
