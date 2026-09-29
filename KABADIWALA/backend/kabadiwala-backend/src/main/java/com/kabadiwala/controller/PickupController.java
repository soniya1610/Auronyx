package com.kabadiwala.controller;

import com.kabadiwala.dto.ApiResponse;
import com.kabadiwala.dto.PickupDto;
import com.kabadiwala.dto.PickupRequest;
import com.kabadiwala.entity.WasteCategory;
import com.kabadiwala.repository.WasteCategoryRepository;
import com.kabadiwala.service.PickupService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pickups")
public class PickupController {

    private final PickupService pickupService;
    private final WasteCategoryRepository categoryRepository;

    public PickupController(PickupService pickupService, WasteCategoryRepository categoryRepository) {
        this.pickupService = pickupService;
        this.categoryRepository = categoryRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PickupDto>> createPickup(@Valid @RequestBody PickupRequest request) {
        PickupDto dto = pickupService.createPickup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Pickup created successfully", dto));
    }

    @PostMapping("/book")
    public ResponseEntity<ApiResponse<PickupDto>> bookPickup(@RequestBody Map<String, Object> req) {
        PickupRequest request = new PickupRequest();
        String wasteType = req.getOrDefault("wasteType", "Plastic").toString();
        
        Long categoryId = 1L;
        List<WasteCategory> categories = categoryRepository.findAll();
        for (WasteCategory cat : categories) {
            if (cat.getName().toLowerCase().contains(wasteType.toLowerCase()) || 
                wasteType.toLowerCase().contains(cat.getName().toLowerCase())) {
                categoryId = cat.getId();
                break;
            }
        }
        if (!categories.isEmpty() && categoryId == 1L && !categories.get(0).getId().equals(1L)) {
            categoryId = categories.get(0).getId();
        }
        request.setWasteCategoryId(categoryId);

        if (req.containsKey("estimatedWeightKg") && req.get("estimatedWeightKg") != null && !req.get("estimatedWeightKg").toString().isBlank()) {
            request.setEstimatedWeight(new BigDecimal(req.get("estimatedWeightKg").toString()));
        } else {
            request.setEstimatedWeight(new BigDecimal("2.0"));
        }

        String addr = req.getOrDefault("address", "123 Green Street").toString();
        request.setAddressLine(addr);
        request.setCity(req.getOrDefault("city", "Mumbai").toString());
        request.setState(req.getOrDefault("state", "Maharashtra").toString());
        request.setPincode(req.getOrDefault("pincode", "400001").toString());

        LocalDate schedDate = LocalDate.now().plusDays(1);
        if (req.containsKey("scheduledDate") && req.get("scheduledDate") != null && !req.get("scheduledDate").toString().isBlank()) {
            try {
                schedDate = LocalDate.parse(req.get("scheduledDate").toString().substring(0, 10));
            } catch (Exception ignored) {}
        }
        request.setScheduledDate(schedDate);
        request.setScheduledTime(req.getOrDefault("timeSlot", "10:00 AM - 12:00 PM").toString());
        request.setNotes(req.containsKey("notes") && req.get("notes") != null ? req.get("notes").toString() : "");

        PickupDto dto = pickupService.createPickup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Pickup booked successfully", dto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PickupDto>>> getMyPickups() {
        List<PickupDto> dtos = pickupService.getMyPickups();
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @GetMapping("/my-pickups")
    public ResponseEntity<ApiResponse<List<PickupDto>>> getMyPickupsAlias() {
        return getMyPickups();
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
