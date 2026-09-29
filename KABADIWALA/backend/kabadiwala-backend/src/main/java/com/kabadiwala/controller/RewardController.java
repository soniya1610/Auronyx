package com.kabadiwala.controller;

import com.kabadiwala.dto.ApiResponse;
import com.kabadiwala.entity.Reward;
import com.kabadiwala.entity.UserReward;
import com.kabadiwala.service.RewardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @GetMapping("/catalog")
    public ResponseEntity<ApiResponse<List<Reward>>> getCatalog() {
        List<Reward> catalog = rewardService.getCatalog();
        return ResponseEntity.ok(ApiResponse.success(catalog));
    }

    @PostMapping("/redeem")
    public ResponseEntity<ApiResponse<UserReward>> redeemReward(@RequestBody Map<String, Object> body) {
        Long rewardId = Long.valueOf(body.get("rewardId").toString());
        UserReward redeemed = rewardService.redeemReward(rewardId);
        return ResponseEntity.ok(ApiResponse.success("Reward redeemed successfully! Coupon code generated.", redeemed));
    }

    @GetMapping("/my-rewards")
    public ResponseEntity<ApiResponse<List<UserReward>>> getMyRewards() {
        List<UserReward> list = rewardService.getMyRedeemedRewards();
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
