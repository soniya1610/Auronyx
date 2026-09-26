package com.kabadiwala.controller;

import com.kabadiwala.dto.ApiResponse;
import com.kabadiwala.dto.PickupDto;
import com.kabadiwala.dto.VerificationDto;
import com.kabadiwala.dto.VerificationRequest;
import com.kabadiwala.service.CollectorOperationsService;
import com.kabadiwala.service.VerificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/collector")
@PreAuthorize("hasRole('COLLECTOR')")
public class CollectorController {

    private final CollectorOperationsService operationsService;
    private final VerificationService verificationService;

    public CollectorController(CollectorOperationsService operationsService,
                                VerificationService verificationService) {
        this.operationsService = operationsService;
        this.verificationService = verificationService;
    }

    @GetMapping("/pickups")
    public ResponseEntity<ApiResponse<List<PickupDto>>> getPickups(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.success(operationsService.getPickups(status)));
    }

    @GetMapping("/pickups/nearby")
    public ResponseEntity<ApiResponse<List<PickupDto>>> getNearbyPickups(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(defaultValue = "25.0") Double radiusKm) {
        return ResponseEntity.ok(ApiResponse.success(operationsService.getNearbyPickups(latitude, longitude, radiusKm)));
    }

    @GetMapping("/pickups/available")
    public ResponseEntity<ApiResponse<List<PickupDto>>> getAvailablePickups() {
        return ResponseEntity.ok(ApiResponse.success(operationsService.getAvailablePickups()));
    }

    @GetMapping("/pickups/assigned")
    public ResponseEntity<ApiResponse<List<PickupDto>>> getMyPickups() {
        return ResponseEntity.ok(ApiResponse.success(operationsService.getMyAssignedPickups()));
    }

    @PostMapping("/pickups/{id}/accept")
    public ResponseEntity<ApiResponse<PickupDto>> acceptPost(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Pickup accepted", operationsService.acceptPickup(id)));
    }

    @PutMapping("/pickups/{id}/accept")
    public ResponseEntity<ApiResponse<PickupDto>> acceptPut(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Pickup accepted", operationsService.acceptPickup(id)));
    }

    @PostMapping("/pickups/{id}/reject")
    public ResponseEntity<ApiResponse<PickupDto>> reject(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String reason = (body != null) ? body.get("reason") : "Collector unable to service pickup";
        return ResponseEntity.ok(ApiResponse.success("Pickup unassigned and returned to queue", operationsService.rejectPickup(id, reason)));
    }

    @PutMapping("/pickups/{id}/status")
    public ResponseEntity<ApiResponse<PickupDto>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = (body != null) ? body.get("status") : null;
        return ResponseEntity.ok(ApiResponse.success("Pickup status updated", operationsService.updateStatus(id, status)));
    }

    @PutMapping("/pickups/{id}/on-the-way")
    public ResponseEntity<ApiResponse<PickupDto>> onTheWay(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Status updated to ON_THE_WAY", operationsService.markOnTheWay(id)));
    }

    @PutMapping("/pickups/{id}/collected")
    public ResponseEntity<ApiResponse<PickupDto>> collected(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Waste collected", operationsService.markCollected(id)));
    }

    @GetMapping("/pickups/{id}/verification")
    public ResponseEntity<ApiResponse<VerificationDto>> getPickupVerification(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(verificationService.getVerificationByPickupId(id)));
    }

    @PostMapping("/pickups/{id}/verification")
    public ResponseEntity<ApiResponse<VerificationDto>> postPickupVerification(
            @PathVariable Long id,
            @Valid @RequestBody VerificationRequest request) {
        request.setPickupId(id);
        return ResponseEntity.ok(ApiResponse.success("Verification completed, payment credited to user wallet",
                verificationService.verifyPickup(request)));
    }

    @PutMapping("/pickups/{id}/verification")
    public ResponseEntity<ApiResponse<VerificationDto>> putPickupVerification(
            @PathVariable Long id,
            @Valid @RequestBody VerificationRequest request) {
        request.setPickupId(id);
        return ResponseEntity.ok(ApiResponse.success("Verification completed, payment credited to user wallet",
                verificationService.verifyPickup(request)));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<VerificationDto>> verify(@Valid @RequestBody VerificationRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Verification completed, payment credited to user wallet",
                verificationService.verifyPickup(request)));
    }

    @GetMapping("/verify/pickup/{pickupId}")
    public ResponseEntity<ApiResponse<VerificationDto>> getVerification(@PathVariable Long pickupId) {
        return ResponseEntity.ok(ApiResponse.success(verificationService.getVerificationByPickupId(pickupId)));
    }
}
