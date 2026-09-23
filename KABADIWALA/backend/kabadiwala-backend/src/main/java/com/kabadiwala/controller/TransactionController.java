package com.kabadiwala.controller;

import com.kabadiwala.dto.ApiResponse;
import com.kabadiwala.dto.TransactionDto;
import com.kabadiwala.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionDto>>> getMyTransactions() {
        return ResponseEntity.ok(ApiResponse.success(transactionService.getMyTransactions()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionDto>> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.getTransactionById(id)));
    }

    @GetMapping("/pickup/{pickupId}")
    public ResponseEntity<ApiResponse<TransactionDto>> getTransactionByPickupId(@PathVariable Long pickupId) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.getTransactionByPickupId(pickupId)));
    }
}
