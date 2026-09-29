package com.kabadiwala.controller;

import com.kabadiwala.dto.ApiResponse;
import com.kabadiwala.dto.CollectorAvailabilityRequest;
import com.kabadiwala.dto.CollectorProfileUpdateRequest;
import com.kabadiwala.dto.CollectorResponse;
import com.kabadiwala.service.CollectorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CollectorController {

    private final CollectorService collectorService;

    public CollectorController(CollectorService collectorService) {
        this.collectorService = collectorService;
    }

    // Public Discovery APIs
    @GetMapping("/api/collectors")
    public ResponseEntity<ApiResponse<List<CollectorResponse>>> getAllCollectors() {
        List<CollectorResponse> collectors = collectorService.getAllCollectors();
        return ResponseEntity.ok(ApiResponse.success(collectors));
    }

    @GetMapping("/api/collectors/nearby")
    public ResponseEntity<ApiResponse<List<CollectorResponse>>> getNearbyCollectors(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false, defaultValue = "10.0") Double radiusKm
    ) {
        List<CollectorResponse> nearby = collectorService.getNearbyCollectors(latitude, longitude, radiusKm);
        return ResponseEntity.ok(ApiResponse.success(nearby));
    }

    @GetMapping("/api/collectors/{id}")
    public ResponseEntity<ApiResponse<CollectorResponse>> getCollectorById(@PathVariable Long id) {
        CollectorResponse collector = collectorService.getCollectorById(id);
        return ResponseEntity.ok(ApiResponse.success(collector));
    }

    // Authenticated Collector Profile APIs
    @GetMapping("/api/collector/profile")
    public ResponseEntity<ApiResponse<CollectorResponse>> getCollectorProfile() {
        CollectorResponse response = collectorService.getProfile();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/api/collector/profile")
    public ResponseEntity<ApiResponse<CollectorResponse>> updateCollectorProfile(
            @Valid @RequestBody CollectorProfileUpdateRequest request
    ) {
        CollectorResponse response = collectorService.updateProfile(request);
        return ResponseEntity.ok(ApiResponse.success("Collector profile updated successfully", response));
    }

    @PutMapping("/api/collector/availability")
    public ResponseEntity<ApiResponse<CollectorResponse>> updateCollectorAvailability(
            @Valid @RequestBody CollectorAvailabilityRequest request
    ) {
        CollectorResponse response = collectorService.updateAvailability(request);
        return ResponseEntity.ok(ApiResponse.success("Collector availability updated successfully", response));
    }
}
