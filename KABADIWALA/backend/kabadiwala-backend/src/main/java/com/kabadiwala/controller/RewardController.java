package com.kabadiwala.controller;

import com.kabadiwala.dto.ApiResponse;
import com.kabadiwala.dto.RedemptionRequest;
import com.kabadiwala.entity.Redemption;
import com.kabadiwala.entity.Reward;
import com.kabadiwala.service.GamificationService;
import com.kabadiwala.service.PointService;
import com.kabadiwala.service.RedemptionService;
import com.kabadiwala.service.RewardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * RewardController — Module 3 complete implementation.
 * Reward catalog, points balance, and redemption endpoints.
 */
@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    private final RewardService rewardService;
    private final RedemptionService redemptionService;
    private final PointService pointService;
    private final GamificationService gamificationService;

    public RewardController(RewardService rewardService,
                             RedemptionService redemptionService,
                             PointService pointService,
                             GamificationService gamificationService) {
        this.rewardService      = rewardService;
        this.redemptionService  = redemptionService;
        this.pointService       = pointService;
        this.gamificationService = gamificationService;
    }

    // ---- Reward Catalog ----

    @GetMapping
    public ResponseEntity<ApiResponse<List<Reward>>> getAvailableRewards() {
        return ResponseEntity.ok(ApiResponse.success("Available rewards retrieved",
                rewardService.getAllActiveRewards()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Reward>> getRewardById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Reward retrieved", rewardService.getRewardById(id)));
    }

    // ---- Redemption ----

    /**
     * POST /api/rewards/redeem
     * Redeem a reward using the authenticated user's points.
     * Validates balance, deducts points, credits wallet for CASH_BACK rewards.
     */
    @PostMapping("/redeem")
    public ResponseEntity<ApiResponse<Redemption>> redeemReward(@Valid @RequestBody RedemptionRequest request) {
        Redemption redemption = redemptionService.redeemReward(request.getRewardId());
        return ResponseEntity.ok(ApiResponse.success("Reward redeemed successfully", redemption));
    }

    @GetMapping("/my-redemptions")
    public ResponseEntity<ApiResponse<List<Redemption>>> getMyRedemptions() {
        return ResponseEntity.ok(ApiResponse.success("Your redemptions retrieved",
                redemptionService.getMyRedemptions()));
    }

    // ---- Points ----

    @GetMapping("/points/balance")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getMyPointsBalance() {
        return ResponseEntity.ok(ApiResponse.success("Points balance retrieved",
                pointService.getMyBalance()));
    }

    @GetMapping("/points/history")
    public ResponseEntity<ApiResponse<?>> getMyPointsHistory() {
        return ResponseEntity.ok(ApiResponse.success("Points history retrieved",
                pointService.getMyHistory()));
    }

    // ---- Gamification ----

    @GetMapping("/gamification/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getGamificationSummary() {
        Long userId = com.kabadiwala.security.SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success("Gamification summary retrieved",
                gamificationService.getUserGamificationSummary(userId)));
    }

    @GetMapping("/gamification/badges")
    public ResponseEntity<ApiResponse<?>> getMyBadges() {
        Long userId = com.kabadiwala.security.SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success("Badges retrieved",
                gamificationService.getEarnedBadges(userId)));
    }

    @GetMapping("/gamification/challenges")
    public ResponseEntity<ApiResponse<?>> getActiveChallenges() {
        return ResponseEntity.ok(ApiResponse.success("Active challenges retrieved",
                gamificationService.getActiveChallenges()));
    }

    // ---- Admin: Reward Management ----

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Reward>> createReward(@RequestBody Reward reward) {
        return ResponseEntity.ok(ApiResponse.success("Reward created", rewardService.createReward(reward)));
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Reward>> updateReward(@PathVariable Long id,
                                                             @RequestBody Reward reward) {
        return ResponseEntity.ok(ApiResponse.success("Reward updated", rewardService.updateReward(id, reward)));
    }

    @PutMapping("/admin/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Reward>> toggleReward(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Reward status toggled", rewardService.toggleActive(id)));
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteReward(@PathVariable Long id) {
        rewardService.deleteReward(id);
        return ResponseEntity.ok(ApiResponse.success("Reward deleted", null));
    }

    // ---- Admin: Redemption Management ----

    @GetMapping("/admin/all-rewards")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Reward>>> getAllRewardsAdmin() {
        return ResponseEntity.ok(ApiResponse.success("All rewards retrieved",
                rewardService.getAllRewards()));
    }

    @GetMapping("/admin/redemptions/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Redemption>>> getPendingRedemptions() {
        return ResponseEntity.ok(ApiResponse.success("Pending redemptions retrieved",
                redemptionService.getPendingRedemptions()));
    }

    @PutMapping("/admin/redemptions/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Redemption>> approveRedemption(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Redemption approved",
                redemptionService.approveRedemption(id)));
    }

    @PutMapping("/admin/redemptions/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Redemption>> rejectRedemption(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Redemption rejected",
                redemptionService.rejectRedemption(id)));
    }

    // ---- Admin: Badges & Challenges ----

    @GetMapping("/admin/badges")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> getAllBadges() {
        return ResponseEntity.ok(ApiResponse.success("All badges retrieved",
                gamificationService.getAllBadges()));
    }

    @PostMapping("/admin/badges")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> createBadge(@RequestBody com.kabadiwala.entity.Badge badge) {
        return ResponseEntity.ok(ApiResponse.success("Badge created",
                gamificationService.saveBadge(badge)));
    }

    @PostMapping("/admin/challenges")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> createChallenge(@RequestBody com.kabadiwala.entity.Challenge challenge) {
        return ResponseEntity.ok(ApiResponse.success("Challenge created",
                gamificationService.saveChallenge(challenge)));
    }
}
