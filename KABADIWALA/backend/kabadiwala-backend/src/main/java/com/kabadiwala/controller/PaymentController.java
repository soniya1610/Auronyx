package com.kabadiwala.controller;

import com.kabadiwala.dto.*;
import com.kabadiwala.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/wallet")
    public ResponseEntity<ApiResponse<WalletDto>> getWallet() {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getMyWallet()));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<TransactionDto>> withdraw(@Valid @RequestBody WithdrawalRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Withdrawal successful", paymentService.withdraw(request)));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<TransactionDto>>> getTransactions() {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getMyTransactions()));
    }
}
