package com.kabadiwala.service;

import com.kabadiwala.dto.TransactionDto;
import com.kabadiwala.entity.*;
import com.kabadiwala.exception.InvalidTransactionException;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.exception.UnauthorizedException;
import com.kabadiwala.repository.PaymentRepository;
import com.kabadiwala.repository.RecyclingRecordRepository;
import com.kabadiwala.repository.TransactionRepository;
import com.kabadiwala.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final PaymentRepository paymentRepository;
    private final WalletService walletService;
    private final RecyclingRecordRepository recyclingRecordRepository;
    private final PricingService pricingService;

    public TransactionService(TransactionRepository transactionRepository,
                              PaymentRepository paymentRepository,
                              WalletService walletService,
                              RecyclingRecordRepository recyclingRecordRepository,
                              PricingService pricingService) {
        this.transactionRepository = transactionRepository;
        this.paymentRepository = paymentRepository;
        this.walletService = walletService;
        this.recyclingRecordRepository = recyclingRecordRepository;
        this.pricingService = pricingService;
    }

    /**
     * Completes transaction from physical collector verification.
     * Guaranteed server-side rate lookup and BigDecimal calculation.
     */
    @Transactional
    public Transaction processVerifiedPickup(Pickup pickup, WasteVerification verification) {
        if (transactionRepository.existsByPickupId(pickup.getId())) {
            throw new InvalidTransactionException("Transaction already exists for pickup #" + pickup.getId());
        }

        WasteCategory category = verification.getActualCategory();
        BigDecimal actualWeight = verification.getActualWeight();
        BigDecimal appliedRate = pricingService.getPriceForCategory(category.getId());
        BigDecimal finalAmount = pricingService.calculateFinalAmount(category, actualWeight);

        String txnId = "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

        Transaction txn = new Transaction();
        txn.setTransactionId(txnId);
        txn.setPickup(pickup);
        txn.setUser(pickup.getUser());
        txn.setCollector(verification.getCollector());
        txn.setCategory(category);
        txn.setWasteItem(verification.getActualWasteItem());
        txn.setActualWeight(actualWeight);
        txn.setAppliedRate(appliedRate);
        txn.setFinalAmount(finalAmount);
        txn.setStatus(Transaction.Status.COMPLETED);
        txn.setCompletedAt(LocalDateTime.now());
        Transaction savedTxn = transactionRepository.save(txn);

        // 1. Create Payment record
        String paymentId = "PAY-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        Payment payment = new Payment();
        payment.setPaymentId(paymentId);
        payment.setTransaction(savedTxn);
        payment.setAmount(finalAmount);
        payment.setMethod(Payment.Method.WALLET);
        payment.setStatus(Payment.Status.SUCCESS);
        payment.setProviderReference("WALLET_CREDIT");
        payment.setCompletedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        // 2. Credit User Wallet
        Wallet wallet = walletService.getOrCreateWallet(pickup.getUser());
        walletService.credit(wallet, finalAmount, "TRANSACTION", txnId);

        // 3. Initialize Recycling lifecycle record (COLLECTED stage)
        RecyclingRecord rr = new RecyclingRecord();
        rr.setTransaction(savedTxn);
        rr.setPickup(pickup);
        rr.setWasteCategory(category);
        rr.setWasteItem(verification.getActualWasteItem());
        rr.setWeight(actualWeight);
        rr.setCollector(verification.getCollector());
        rr.setStatus(RecyclingRecord.Status.COLLECTED);
        rr.setHandoverInfo("Collected by " + verification.getCollector().getUser().getName());
        recyclingRecordRepository.save(rr);

        return savedTxn;
    }

    @Transactional(readOnly = true)
    public List<TransactionDto> getMyTransactions() {
        Long userId = SecurityUtils.getCurrentUserId();
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TransactionDto getTransactionById(Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Transaction txn = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));
        if (!txn.getUser().getId().equals(currentUserId) &&
                (txn.getCollector() == null || !txn.getCollector().getUser().getId().equals(currentUserId))) {
            throw new UnauthorizedException("You are not authorized to view this transaction.");
        }
        return mapToDto(txn);
    }

    @Transactional(readOnly = true)
    public TransactionDto getTransactionByPickupId(Long pickupId) {
        Transaction txn = transactionRepository.findByPickupId(pickupId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "pickupId", pickupId));
        return mapToDto(txn);
    }

    public TransactionDto mapToDto(Transaction txn) {
        TransactionDto dto = new TransactionDto();
        dto.setId(txn.getId());
        dto.setReferenceNo(txn.getTransactionId());
        dto.setUserId(txn.getUser().getId());
        if (txn.getPickup() != null) dto.setPickupId(txn.getPickup().getId());
        dto.setAmount(txn.getFinalAmount());
        dto.setType("CREDIT");
        dto.setStatus(txn.getStatus().name());
        dto.setMethod("WALLET");
        dto.setDescription("Payment for waste pickup #" + (txn.getPickup() != null ? txn.getPickup().getId() : "") +
                " (" + txn.getActualWeight() + " kg @ ₹" + txn.getAppliedRate() + "/kg)");
        dto.setCreatedAt(txn.getCreatedAt());
        dto.setCompletedAt(txn.getCompletedAt());
        return dto;
    }
}
