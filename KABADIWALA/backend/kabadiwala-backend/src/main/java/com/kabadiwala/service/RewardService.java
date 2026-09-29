package com.kabadiwala.service;

import com.kabadiwala.entity.Reward;
import com.kabadiwala.entity.User;
import com.kabadiwala.entity.UserReward;
import com.kabadiwala.exception.BadRequestException;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.RewardRepository;
import com.kabadiwala.repository.UserRepository;
import com.kabadiwala.repository.UserRewardRepository;
import com.kabadiwala.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RewardService {

    private final RewardRepository rewardRepository;
    private final UserRewardRepository userRewardRepository;
    private final UserRepository userRepository;

    public RewardService(RewardRepository rewardRepository, UserRewardRepository userRewardRepository, UserRepository userRepository) {
        this.rewardRepository = rewardRepository;
        this.userRewardRepository = userRewardRepository;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    public List<Reward> getCatalog() {
        return rewardRepository.findByAvailableTrue();
    }

    @Transactional
    public UserReward redeemReward(Long rewardId) {
        User user = getAuthenticatedUser();
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new ResourceNotFoundException("Reward not found with ID: " + rewardId));

        if (!reward.isAvailable()) {
            throw new BadRequestException("Reward is currently out of stock");
        }

        String couponCode = "KABADI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        UserReward userReward = new UserReward(
                user.getId(),
                reward.getId(),
                reward.getTitle(),
                couponCode,
                reward.getPointsCost()
        );

        return userRewardRepository.save(userReward);
    }

    public List<UserReward> getMyRedeemedRewards() {
        User user = getAuthenticatedUser();
        return userRewardRepository.findByUserIdOrderByRedeemedAtDesc(user.getId());
    }
}
