package com.kabadiwala.controller;

import com.kabadiwala.dto.ApiResponse;
import com.kabadiwala.dto.PickupDto;
import com.kabadiwala.dto.PickupRequest;
import com.kabadiwala.service.PickupService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pickups")
public class PickupController {

    private final PickupService pickupService;

    public PickupController(PickupService pickupService) {
        this.pickupService = pickupService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PickupDto>> createPickup(@Valid @RequestBody PickupRequest request) {
        PickupDto dto = pickupService.createPickup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Pickup created successfully", dto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PickupDto>>> getMyPickups() {
        List<PickupDto> dtos = pickupService.getMyPickups();
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PickupDto>> getPickupById(@PathVariable Long id) {
        PickupDto dto = pickupService.getPickupById(id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PickupDto>> updatePickup(@PathVariable Long id,
                                                               @RequestBody PickupRequest request) {
        PickupDto dto = pickupService.updatePickup(id, request);
        return ResponseEntity.ok(ApiResponse.success("Pickup updated", dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePickup(@PathVariable Long id) {
        pickupService.deletePickup(id);
        return ResponseEntity.ok(ApiResponse.success("Pickup deleted", null));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<ApiResponse<String>> getPickupStatus(@PathVariable Long id) {
        String status = pickupService.getPickupStatus(id);
        return ResponseEntity.ok(ApiResponse.success(status));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<PickupDto>> cancelPickup(@PathVariable Long id) {
        PickupDto dto = pickupService.cancelPickup(id);
        return ResponseEntity.ok(ApiResponse.success("Pickup cancelled", dto));
    }
}
