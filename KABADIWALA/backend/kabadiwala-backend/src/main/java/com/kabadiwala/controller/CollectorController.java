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

    @GetMapping("/pickups/available")
    public ResponseEntity<ApiResponse<List<PickupDto>>> getAvailablePickups() {
        return ResponseEntity.ok(ApiResponse.success(operationsService.getAvailablePickups()));
    }

    @GetMapping("/pickups/assigned")
    public ResponseEntity<ApiResponse<List<PickupDto>>> getMyPickups() {
        return ResponseEntity.ok(ApiResponse.success(operationsService.getMyAssignedPickups()));
    }

    @PutMapping("/pickups/{id}/accept")
    public ResponseEntity<ApiResponse<PickupDto>> accept(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Pickup accepted", operationsService.acceptPickup(id)));
    }

    @PutMapping("/pickups/{id}/on-the-way")
    public ResponseEntity<ApiResponse<PickupDto>> onTheWay(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Status updated to ON_THE_WAY", operationsService.markOnTheWay(id)));
    }

    @PutMapping("/pickups/{id}/collected")
    public ResponseEntity<ApiResponse<PickupDto>> collected(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Waste collected", operationsService.markCollected(id)));
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
