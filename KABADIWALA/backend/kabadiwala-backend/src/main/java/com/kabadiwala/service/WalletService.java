package com.kabadiwala.service;

import com.kabadiwala.entity.*;
import com.kabadiwala.exception.InvalidTransactionException;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.WalletRepository;
import com.kabadiwala.repository.WalletTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    public WalletService(WalletRepository walletRepository,
                         WalletTransactionRepository walletTransactionRepository) {
        this.walletRepository = walletRepository;
        this.walletTransactionRepository = walletTransactionRepository;
    }

    @Transactional
    public Wallet getOrCreateWallet(User user) {
        return walletRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Wallet w = new Wallet(user);
                    w.setBalance(BigDecimal.ZERO);
                    w.setCurrency("INR");
                    return walletRepository.save(w);
                });
    }

    @Transactional(readOnly = true)
    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", "userId", userId));
    }

    /**
     * Credit wallet balance for a completed transaction.
     * Guaranteed idempotent using unique constraint on (wallet_id, reference_type, reference_id).
     */
    @Transactional
    public WalletTransaction credit(Wallet wallet, BigDecimal amount, String referenceType, String referenceId) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Credit amount must be positive");
        }

        if (walletTransactionRepository.existsByWalletIdAndReferenceTypeAndReferenceId(
                wallet.getId(), referenceType, referenceId)) {
            throw new InvalidTransactionException("Wallet credit already processed for " + referenceType + " #" + referenceId);
        }

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        WalletTransaction wt = new WalletTransaction(
                wallet,
                WalletTransaction.Type.CREDIT,
                amount,
                referenceType,
                referenceId
        );
        return walletTransactionRepository.save(wt);
    }

    /**
     * Debit wallet balance (e.g. for withdrawal or redemption).
     */
    @Transactional
    public WalletTransaction debit(Wallet wallet, BigDecimal amount, String referenceType, String referenceId) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Debit amount must be positive");
        }

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InvalidTransactionException("Insufficient wallet balance. Available: ₹" + wallet.getBalance());
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        WalletTransaction wt = new WalletTransaction(
                wallet,
                WalletTransaction.Type.DEBIT,
                amount,
                referenceType,
                referenceId
        );
        return walletTransactionRepository.save(wt);
    }

    @Transactional(readOnly = true)
    public List<WalletTransaction> getWalletTransactions(Long walletId) {
        return walletTransactionRepository.findByWalletIdOrderByCreatedAtDesc(walletId);
    }

    @Transactional(readOnly = true)
    public WalletTransaction getWalletTransactionById(Long transactionId, Long userId) {
        WalletTransaction wt = walletTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("WalletTransaction", "id", transactionId));
        if (!wt.getWallet().getUser().getId().equals(userId)) {
            throw new com.kabadiwala.exception.UnauthorizedException("Access denied: You do not own this wallet transaction.");
        }
        return wt;
    }
}
