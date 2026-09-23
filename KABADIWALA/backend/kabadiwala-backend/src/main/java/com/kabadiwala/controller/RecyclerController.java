package com.kabadiwala.controller;

import com.kabadiwala.dto.ApiResponse;
import com.kabadiwala.dto.RecyclingRecordDto;
import com.kabadiwala.dto.RecyclingUpdateRequest;
import com.kabadiwala.service.RecyclingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recycler")
@PreAuthorize("hasRole('RECYCLER')")
public class RecyclerController {

    private final RecyclingService recyclingService;

    public RecyclerController(RecyclingService recyclingService) {
        this.recyclingService = recyclingService;
    }

    @GetMapping("/incoming")
    public ResponseEntity<ApiResponse<List<RecyclingRecordDto>>> getIncomingWaste() {
        return ResponseEntity.ok(ApiResponse.success(recyclingService.getIncomingRecords()));
    }

    @GetMapping("/records")
    public ResponseEntity<ApiResponse<List<RecyclingRecordDto>>> getMyRecords() {
        return ResponseEntity.ok(ApiResponse.success(recyclingService.getMyRecyclerRecords()));
    }

    @PostMapping("/records/{id}/receive")
    public ResponseEntity<ApiResponse<RecyclingRecordDto>> receiveWaste(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Waste received successfully", recyclingService.receiveWaste(id)));
    }

    @PutMapping("/records/{id}/status")
    public ResponseEntity<ApiResponse<RecyclingRecordDto>> updateStatus(@PathVariable Long id,
                                                                        @Valid @RequestBody RecyclingUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Recycling status updated", recyclingService.updateStatus(id, request)));
    }
}
