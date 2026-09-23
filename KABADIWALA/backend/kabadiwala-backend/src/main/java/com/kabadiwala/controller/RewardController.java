package com.kabadiwala.controller;

import com.kabadiwala.dto.ApiResponse;
import com.kabadiwala.entity.Reward;
import com.kabadiwala.service.RedemptionService;
import com.kabadiwala.service.RewardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RewardController — Module 3 stub.
 * Reward catalog and redemption endpoints.
 * Full implementation deferred to Rewards & Ecosystem Module.
 */
@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    private final RewardService rewardService;
    private final RedemptionService redemptionService;

    public RewardController(RewardService rewardService,
                             RedemptionService redemptionService) {
        this.rewardService = rewardService;
        this.redemptionService = redemptionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Reward>>> getAvailableRewards() {
        return ResponseEntity.ok(ApiResponse.success("Available rewards retrieved",
                rewardService.getAllActiveRewards()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Reward>> getRewardById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Reward retrieved", rewardService.getRewardById(id)));
    }

    @GetMapping("/my-redemptions")
    public ResponseEntity<ApiResponse<?>> getMyRedemptions() {
        return ResponseEntity.ok(ApiResponse.success("Your redemptions retrieved",
                redemptionService.getMyRedemptions()));
    }
}
