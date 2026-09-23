package com.kabadiwala.controller;

import com.kabadiwala.dto.ApiResponse;
import com.kabadiwala.entity.FraudAlert;
import com.kabadiwala.service.AnalyticsService;
import com.kabadiwala.service.FraudDetectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AdminController — Module 2.
 * Admin-only endpoints for platform analytics, fraud management, and system health.
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AnalyticsService analyticsService;
    private final FraudDetectionService fraudDetectionService;

    public AdminController(AnalyticsService analyticsService,
                            FraudDetectionService fraudDetectionService) {
        this.analyticsService = analyticsService;
        this.fraudDetectionService = fraudDetectionService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success("Admin dashboard stats retrieved",
                analyticsService.getAdminSummary()));
    }

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

    @PutMapping("/fraud/alerts/{id}/resolve")
    public ResponseEntity<ApiResponse<FraudAlert>> resolveAlert(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "Reviewed by admin") String notes) {
        FraudAlert resolved = fraudDetectionService.resolveAlert(id, notes);
        return ResponseEntity.ok(ApiResponse.success("Fraud alert resolved", resolved));
    }
}
