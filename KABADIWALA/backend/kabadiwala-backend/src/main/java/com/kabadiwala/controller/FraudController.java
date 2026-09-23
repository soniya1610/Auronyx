package com.kabadiwala.controller;

import com.kabadiwala.dto.ApiResponse;
import com.kabadiwala.entity.FraudAlert;
import com.kabadiwala.service.FraudDetectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * FraudController — Module 2.
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

    @PutMapping("/alerts/{id}/resolve")
    public ResponseEntity<ApiResponse<FraudAlert>> resolveAlert(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "Manually resolved") String notes) {
        FraudAlert resolved = fraudDetectionService.resolveAlert(id, notes);
        return ResponseEntity.ok(ApiResponse.success("Fraud alert resolved", resolved));
    }
}
