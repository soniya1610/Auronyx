package com.kabadiwala.controller;

import com.kabadiwala.dto.ApiResponse;
import com.kabadiwala.entity.FraudAlert;
import com.kabadiwala.entity.User;
import com.kabadiwala.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AdminController — Modules 2 & 3.
 * Admin-only endpoints for platform analytics, fraud management, rewards admin, and system health.
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AnalyticsService analyticsService;
    private final FraudDetectionService fraudDetectionService;
    private final GamificationService gamificationService;
    private final PointService pointService;
    private final UserService userService;

    public AdminController(AnalyticsService analyticsService,
                            FraudDetectionService fraudDetectionService,
                            GamificationService gamificationService,
                            PointService pointService,
                            UserService userService) {
        this.analyticsService     = analyticsService;
        this.fraudDetectionService = fraudDetectionService;
        this.gamificationService  = gamificationService;
        this.pointService         = pointService;
        this.userService          = userService;
    }

    // ---- Platform Dashboard ----

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success("Admin dashboard stats retrieved",
                analyticsService.getAdminSummary()));
    }

    @GetMapping("/analytics/rewards")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRewardsAnalytics() {
        return ResponseEntity.ok(ApiResponse.success("Rewards analytics retrieved",
                analyticsService.getRewardsAnalytics()));
    }

    // ---- Fraud Management ----

    @GetMapping("/fraud/alerts")
    public ResponseEntity<ApiResponse<List<FraudAlert>>> getAllAlerts() {
        return ResponseEntity.ok(ApiResponse.success("Fraud alerts retrieved",
                fraudDetectionService.getAllAlerts()));
    }

    @GetMapping("/fraud/alerts/open")
    public ResponseEntity<ApiResponse<List<FraudAlert>>> getOpenAlerts() {
        return ResponseEntity.ok(ApiResponse.success("Open fraud alerts retrieved",
                fraudDetectionService.getOpenAlerts()));
    }

    @GetMapping("/fraud/alerts/user/{userId}")
    public ResponseEntity<ApiResponse<List<FraudAlert>>> getAlertsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("User fraud alerts retrieved",
                fraudDetectionService.getAlertsByUser(userId)));
    }

    @PutMapping("/fraud/alerts/{id}/resolve")
    public ResponseEntity<ApiResponse<FraudAlert>> resolveAlert(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "Reviewed by admin") String notes) {
        FraudAlert resolved = fraudDetectionService.resolveAlert(id, notes);
        return ResponseEntity.ok(ApiResponse.success("Fraud alert resolved", resolved));
    }

    @PutMapping("/fraud/alerts/{id}/false-positive")
    public ResponseEntity<ApiResponse<FraudAlert>> markFalsePositive(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "Marked as false positive") String notes) {
        FraudAlert updated = fraudDetectionService.markFalsePositive(id, notes);
        return ResponseEntity.ok(ApiResponse.success("Fraud alert marked as false positive", updated));
    }

    // ---- User Points Management ----

    @PostMapping("/users/{userId}/points/award")
    public ResponseEntity<ApiResponse<?>> awardPointsToUser(
            @PathVariable Long userId,
            @RequestParam int points,
            @RequestParam(defaultValue = "Admin bonus") String reason) {
        User user = userService.getUserById(userId);
        var ledger = gamificationService.adminAwardPoints(user, points, reason);
        return ResponseEntity.ok(ApiResponse.success("Points awarded to user " + userId, ledger));
    }

    @GetMapping("/users/{userId}/points/balance")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getUserPointsBalance(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("User points balance",
                pointService.getBalanceForUser(userId)));
    }

    @GetMapping("/users/{userId}/points/history")
    public ResponseEntity<ApiResponse<?>> getUserPointsHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("User points history",
                pointService.getHistoryForUser(userId)));
    }

    @GetMapping("/users/{userId}/gamification/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserGamificationSummary(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("User gamification summary",
                gamificationService.getUserGamificationSummary(userId)));
    }
}
