package com.kabadiwala.controller;

import com.kabadiwala.dto.ApiResponse;
import com.kabadiwala.entity.WasteCategory;
import com.kabadiwala.service.WasteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/waste")
public class WasteController {

    private final WasteService wasteService;

    public WasteController(WasteService wasteService) {
        this.wasteService = wasteService;
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<WasteCategory>>> getCategories() {
        List<WasteCategory> categories = wasteService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @PostMapping("/estimate-price")
    public ResponseEntity<ApiResponse<Map<String, Object>>> estimatePrice(@RequestBody Map<String, Object> request) {
        String category = request.getOrDefault("category", "PLASTIC").toString();
        Double weight = 1.0;
        if (request.containsKey("weightKg")) {
            weight = Double.valueOf(request.get("weightKg").toString());
        }
        Map<String, Object> estimate = wasteService.estimatePrice(category, weight);
        return ResponseEntity.ok(ApiResponse.success(estimate));
    }
}
