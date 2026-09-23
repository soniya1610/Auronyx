package com.kabadiwala.controller;

import com.kabadiwala.dto.ApiResponse;
import com.kabadiwala.dto.WasteCategoryDto;
import com.kabadiwala.dto.WasteItemDto;
import com.kabadiwala.dto.WastePricingDto;
import com.kabadiwala.service.PricingService;
import com.kabadiwala.service.WasteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/waste")
public class WasteController {

    private final WasteService wasteService;
    private final PricingService pricingService;

    public WasteController(WasteService wasteService, PricingService pricingService) {
        this.wasteService = wasteService;
        this.pricingService = pricingService;
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<WasteCategoryDto>>> getAllCategories() {
        List<WasteCategoryDto> categories = wasteService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<WasteCategoryDto>> getCategoryById(@PathVariable Long id) {
        WasteCategoryDto category = wasteService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(category));
    }

    @GetMapping("/items")
    public ResponseEntity<ApiResponse<List<WasteItemDto>>> getAllItems(
            @RequestParam(required = false) Long categoryId) {
        List<WasteItemDto> items = categoryId != null
                ? wasteService.getItemsByCategoryId(categoryId)
                : wasteService.getAllItems();
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<ApiResponse<WasteItemDto>> getItemById(@PathVariable Long id) {
        WasteItemDto item = wasteService.getItemById(id);
        return ResponseEntity.ok(ApiResponse.success(item));
    }

    @GetMapping("/prices")
    public ResponseEntity<ApiResponse<List<WastePricingDto>>> getAllPrices() {
        List<WastePricingDto> prices = pricingService.getAllActivePrices();
        return ResponseEntity.ok(ApiResponse.success(prices));
    }

    @GetMapping("/prices/{categoryId}")
    public ResponseEntity<ApiResponse<WastePricingDto>> getPriceByCategoryId(@PathVariable Long categoryId) {
        WastePricingDto pricing = pricingService.getActivePricingByCategoryId(categoryId);
        return ResponseEntity.ok(ApiResponse.success(pricing));
    }
}
