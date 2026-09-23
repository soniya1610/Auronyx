package com.kabadiwala.controller;

import com.kabadiwala.dto.ApiResponse;
import com.kabadiwala.entity.EPRRecord;
import com.kabadiwala.service.EPRService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * EPRController — Module 3 stub.
 * Extended Producer Responsibility compliance endpoints.
 * Full implementation deferred to Rewards & Ecosystem Module.
 */
@RestController
@RequestMapping("/api/epr")
public class EPRController {

    private final EPRService eprService;

    public EPRController(EPRService eprService) {
        this.eprService = eprService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<EPRRecord>>> getAllRecords() {
        return ResponseEntity.ok(ApiResponse.success("EPR records retrieved", eprService.getAllEPRRecords()));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('RECYCLER')")
    public ResponseEntity<ApiResponse<List<EPRRecord>>> getMyRecords() {
        return ResponseEntity.ok(ApiResponse.success("Your EPR records retrieved", eprService.getMyEPRRecords()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECYCLER')")
    public ResponseEntity<ApiResponse<EPRRecord>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("EPR record retrieved", eprService.getEPRRecordById(id)));
    }

    @PutMapping("/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EPRRecord>> verifyRecord(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("EPR record verified", eprService.verifyEPRRecord(id)));
    }
}
