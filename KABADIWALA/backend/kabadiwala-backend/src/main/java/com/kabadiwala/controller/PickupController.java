package com.kabadiwala.controller;

import com.kabadiwala.dto.ApiResponse;
import com.kabadiwala.entity.Pickup;
import com.kabadiwala.service.PickupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class PickupController {

    private final PickupService pickupService;

    public PickupController(PickupService pickupService) {
        this.pickupService = pickupService;
    }

    // Citizen Pickup endpoints
    @PostMapping("/api/pickups/book")
    public ResponseEntity<ApiResponse<Pickup>> bookPickup(@RequestBody Map<String, Object> request) {
        Pickup pickup = pickupService.bookPickup(request);
        return ResponseEntity.ok(ApiResponse.success("Pickup scheduled successfully", pickup));
    }

    @GetMapping("/api/pickups/my-pickups")
    public ResponseEntity<ApiResponse<List<Pickup>>> getMyPickups() {
        List<Pickup> pickups = pickupService.getUserPickups();
        return ResponseEntity.ok(ApiResponse.success(pickups));
    }

    @GetMapping("/api/pickups/{id}")
    public ResponseEntity<ApiResponse<Pickup>> getPickupById(@PathVariable Long id) {
        Pickup pickup = pickupService.getPickupById(id);
        return ResponseEntity.ok(ApiResponse.success(pickup));
    }

    // Collector Pickup endpoints
    @GetMapping("/api/collector/pickups")
    public ResponseEntity<ApiResponse<List<Pickup>>> getCollectorPickups() {
        List<Pickup> list = pickupService.getCollectorPickups();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PutMapping("/api/collector/pickups/{id}/accept")
    public ResponseEntity<ApiResponse<Pickup>> acceptPickup(@PathVariable Long id) {
        Pickup pickup = pickupService.acceptPickup(id);
        return ResponseEntity.ok(ApiResponse.success("Pickup accepted", pickup));
    }

    @PutMapping("/api/collector/pickups/{id}/verify-weight")
    public ResponseEntity<ApiResponse<Pickup>> verifyWeight(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body
    ) {
        Double weight = Double.valueOf(body.getOrDefault("actualWeightKg", 2.0).toString());
        Pickup pickup = pickupService.verifyWeight(id, weight);
        return ResponseEntity.ok(ApiResponse.success("Weight verified and final payout calculated", pickup));
    }

    @PutMapping("/api/collector/pickups/{id}/complete")
    public ResponseEntity<ApiResponse<Pickup>> completePickup(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Object> body
    ) {
        String paymentMethod = body != null ? body.getOrDefault("paymentMethod", "WALLET").toString() : "WALLET";
        Pickup pickup = pickupService.completePickup(id, paymentMethod);
        return ResponseEntity.ok(ApiResponse.success("Pickup marked as completed and payout sent", pickup));
    }
}
