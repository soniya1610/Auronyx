package com.kabadiwala.controller;

import com.kabadiwala.dto.ApiResponse;
import com.kabadiwala.dto.WalletDto;
import com.kabadiwala.entity.User;
import com.kabadiwala.entity.Wallet;
import com.kabadiwala.entity.WalletTransaction;
import com.kabadiwala.security.SecurityUtils;
import com.kabadiwala.service.PaymentService;
import com.kabadiwala.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;
    private final PaymentService paymentService;

    public WalletController(WalletService walletService, PaymentService paymentService) {
        this.walletService = walletService;
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<WalletDto>> getWallet() {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getMyWallet()));
    }

    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<BigDecimal>> getBalance() {
        User user = SecurityUtils.getCurrentUser();
        Wallet wallet = walletService.getOrCreateWallet(user);
        return ResponseEntity.ok(ApiResponse.success(wallet.getBalance()));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<WalletTransaction>>> getTransactions() {
        User user = SecurityUtils.getCurrentUser();
        Wallet wallet = walletService.getOrCreateWallet(user);
        return ResponseEntity.ok(ApiResponse.success(walletService.getWalletTransactions(wallet.getId())));
    }

    @GetMapping("/transactions/{id}")
    public ResponseEntity<ApiResponse<WalletTransaction>> getTransactionById(@org.springframework.web.bind.annotation.PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(walletService.getWalletTransactionById(id, userId)));
    }
}
