package com.kabadiwala.controller;

import com.kabadiwala.dto.ApiResponse;
import com.kabadiwala.dto.RecyclingRecordDto;
import com.kabadiwala.service.RecyclingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recycling")
public class RecyclingController {

    private final RecyclingService recyclingService;

    public RecyclingController(RecyclingService recyclingService) {
        this.recyclingService = recyclingService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RecyclingRecordDto>>> getMyRecyclingRecords() {
        return ResponseEntity.ok(ApiResponse.success(recyclingService.getMyRecyclingRecords()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RecyclingRecordDto>> getRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(recyclingService.getRecordById(id)));
    }

    @GetMapping("/pickup/{pickupId}")
    public ResponseEntity<ApiResponse<RecyclingRecordDto>> getRecordByPickupId(@PathVariable Long pickupId) {
        return ResponseEntity.ok(ApiResponse.success(recyclingService.getRecordByPickupId(pickupId)));
    }
}
