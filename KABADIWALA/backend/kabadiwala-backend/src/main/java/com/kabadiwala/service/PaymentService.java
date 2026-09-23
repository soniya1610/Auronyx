package com.kabadiwala.service;

import com.kabadiwala.dto.TransactionDto;
import com.kabadiwala.dto.WalletDto;
import com.kabadiwala.dto.WithdrawalRequest;
import com.kabadiwala.entity.User;
import com.kabadiwala.entity.Wallet;
import com.kabadiwala.entity.WalletTransaction;
import com.kabadiwala.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final WalletService walletService;
    private final TransactionService transactionService;

    public PaymentService(WalletService walletService, TransactionService transactionService) {
        this.walletService = walletService;
        this.transactionService = transactionService;
    }

    @Transactional(readOnly = true)
    public WalletDto getMyWallet() {
        User user = SecurityUtils.getCurrentUser();
        Wallet wallet = walletService.getOrCreateWallet(user);
        return mapWallet(wallet);
    }

    @Transactional
    public TransactionDto withdraw(WithdrawalRequest request) {
        User user = SecurityUtils.getCurrentUser();
        Wallet wallet = walletService.getOrCreateWallet(user);

        String refId = "WDR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        WalletTransaction wt = walletService.debit(wallet, request.getAmount(), "WITHDRAWAL", refId);

        TransactionDto dto = new TransactionDto();
        dto.setReferenceNo(refId);
        dto.setUserId(user.getId());
        dto.setAmount(request.getAmount());
        dto.setType("DEBIT");
        dto.setStatus("COMPLETED");
        dto.setMethod(request.getMethod().toUpperCase());
        dto.setDescription("Withdrawal via " + request.getMethod().toUpperCase());
        dto.setCreatedAt(wt.getCreatedAt());
        dto.setCompletedAt(LocalDateTime.now());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<TransactionDto> getMyTransactions() {
        return transactionService.getMyTransactions();
    }

    public WalletDto mapWallet(Wallet w) {
        WalletDto dto = new WalletDto();
        dto.setId(w.getId());
        dto.setUserId(w.getUser().getId());
        dto.setBalance(w.getBalance());
        dto.setTotalEarned(w.getBalance()); // current balance
        dto.setTotalWithdrawn(w.getBalance());
        dto.setUpdatedAt(w.getUpdatedAt());
        return dto;
    }
}
