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

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentDto>> createPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentDto dto = paymentService.processPayment(request);
        return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentDto>> getPaymentById(@PathVariable Long id) {
        PaymentDto dto = paymentService.getPaymentById(id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<ApiResponse<String>> getPaymentStatus(@PathVariable Long id) {
        String status = paymentService.getPaymentStatus(id);
        return ResponseEntity.ok(ApiResponse.success(status));
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
