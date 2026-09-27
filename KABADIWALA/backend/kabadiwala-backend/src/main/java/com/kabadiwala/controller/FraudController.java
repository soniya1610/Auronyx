package com.kabadiwala.controller;

import com.kabadiwala.dto.ApiResponse;
import com.kabadiwala.entity.FraudAlert;
import com.kabadiwala.service.FraudDetectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * FraudController — Modules 2 & 3.
 * Dedicated fraud management REST endpoints (admin-facing).
 */
@RestController
@RequestMapping("/api/fraud")
@PreAuthorize("hasRole('ADMIN')")
public class FraudController {

    private final FraudDetectionService fraudDetectionService;

    public FraudController(FraudDetectionService fraudDetectionService) {
        this.fraudDetectionService = fraudDetectionService;
    }

    @GetMapping("/alerts")
    public ResponseEntity<ApiResponse<List<FraudAlert>>> getAllAlerts() {
        return ResponseEntity.ok(ApiResponse.success("All fraud alerts retrieved",
                fraudDetectionService.getAllAlerts()));
    }

    @GetMapping("/alerts/open")
    public ResponseEntity<ApiResponse<List<FraudAlert>>> getOpenAlerts() {
        return ResponseEntity.ok(ApiResponse.success("Open fraud alerts retrieved",
                fraudDetectionService.getOpenAlerts()));
    }

    @GetMapping("/alerts/user/{userId}")
    public ResponseEntity<ApiResponse<List<FraudAlert>>> getAlertsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("User fraud alerts retrieved",
                fraudDetectionService.getAlertsByUser(userId)));
    }

    @PutMapping("/alerts/{id}/resolve")
    public ResponseEntity<ApiResponse<FraudAlert>> resolveAlert(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "Manually resolved") String notes) {
        FraudAlert resolved = fraudDetectionService.resolveAlert(id, notes);
        return ResponseEntity.ok(ApiResponse.success("Fraud alert resolved", resolved));
    }

    @PutMapping("/alerts/{id}/false-positive")
    public ResponseEntity<ApiResponse<FraudAlert>> markFalsePositive(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "False positive") String notes) {
        FraudAlert updated = fraudDetectionService.markFalsePositive(id, notes);
        return ResponseEntity.ok(ApiResponse.success("Fraud alert marked as false positive", updated));
    }
}
