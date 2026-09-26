package com.kabadiwala.service;

import com.kabadiwala.dto.PaymentDto;
import com.kabadiwala.dto.PaymentRequest;
import com.kabadiwala.dto.TransactionDto;
import com.kabadiwala.dto.WalletDto;
import com.kabadiwala.dto.WithdrawalRequest;
import com.kabadiwala.entity.Payment;
import com.kabadiwala.entity.Transaction;
import com.kabadiwala.entity.User;
import com.kabadiwala.entity.Wallet;
import com.kabadiwala.entity.WalletTransaction;
import com.kabadiwala.exception.InvalidTransactionException;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.exception.UnauthorizedException;
import com.kabadiwala.repository.PaymentRepository;
import com.kabadiwala.repository.TransactionRepository;
import com.kabadiwala.security.SecurityUtils;
import com.kabadiwala.service.payment.MockPaymentProvider;
import com.kabadiwala.service.payment.PaymentProvider;
import com.kabadiwala.service.payment.PaymentResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final WalletService walletService;
    private final TransactionService transactionService;
    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentProvider paymentProvider;
    private final NotificationService notificationService;

    @org.springframework.beans.factory.annotation.Autowired
    public PaymentService(WalletService walletService,
                          TransactionService transactionService,
                          PaymentRepository paymentRepository,
                          TransactionRepository transactionRepository,
                          PaymentProvider paymentProvider,
                          NotificationService notificationService) {
        this.walletService = walletService;
        this.transactionService = transactionService;
        this.paymentRepository = paymentRepository;
        this.transactionRepository = transactionRepository;
        this.paymentProvider = paymentProvider;
        this.notificationService = notificationService;
    }

    // Overloaded constructor for backwards compatibility with tests
    public PaymentService(WalletService walletService, TransactionService transactionService) {
        this(walletService, transactionService, null, null, new MockPaymentProvider(), null);
    }

    /**
     * Process payment for a verified transaction.
     * CRITICAL SECURITY:
     * - Payment amount MUST come directly from backend transaction.
     * - Any client-supplied amount is strictly ignored.
     * - Payment failure MUST NOT credit wallet.
     * - Duplicate payment processing is rejected.
     */
    @Transactional
    public PaymentDto processPayment(PaymentRequest request) {
        Transaction transaction;
        if (request.getPickupId() != null) {
            transaction = transactionRepository.findByPickupId(request.getPickupId())
                    .orElseThrow(() -> new ResourceNotFoundException("Transaction for pickup #" + request.getPickupId() + " not found"));
        } else {
            throw new InvalidTransactionException("Pickup ID is required to process payment.");
        }

        // Prevent duplicate successful payment
        List<Payment> existingPayments = paymentRepository.findByTransactionIdOrderByCreatedAtDesc(transaction.getId());
        boolean alreadySucceeded = existingPayments.stream().anyMatch(p -> p.getStatus() == Payment.Status.SUCCESS);
        if (alreadySucceeded) {
            throw new InvalidTransactionException("Payment already processed successfully for transaction #" + transaction.getTransactionId());
        }

        // Server-side authoritative amount
        BigDecimal authoritativeAmount = transaction.getFinalAmount();

        Payment.Method method = Payment.Method.WALLET;
        if (request.getPaymentMethod() != null) {
            try {
                method = Payment.Method.valueOf(request.getPaymentMethod().toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        // Call payment provider abstraction
        PaymentResult result = paymentProvider.processPayment(transaction.getTransactionId(), authoritativeAmount, method);

        String paymentId = "PAY-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        Payment payment = new Payment();
        payment.setPaymentId(paymentId);
        payment.setTransaction(transaction);
        payment.setAmount(authoritativeAmount);
        payment.setMethod(method);
        payment.setProviderReference(result.getProviderReference());

        if (result.isSuccess()) {
            payment.setStatus(Payment.Status.SUCCESS);
            payment.setCompletedAt(LocalDateTime.now());
            Payment saved = paymentRepository.save(payment);

            // ONLY on success, credit wallet
            Wallet wallet = walletService.getOrCreateWallet(transaction.getUser());
            walletService.credit(wallet, authoritativeAmount, "TRANSACTION", transaction.getTransactionId());

            if (notificationService != null) {
                notificationService.sendNotification(transaction.getUser(), "Payment Processed",
                        "Payment of ₹" + authoritativeAmount + " completed successfully.", "PAYMENT_SUCCESS");
            }

            return mapPayment(saved);
        } else {
            payment.setStatus(Payment.Status.FAILED);
            Payment saved = paymentRepository.save(payment);

            if (notificationService != null) {
                notificationService.sendNotification(transaction.getUser(), "Payment Failed",
                        "Payment processing failed: " + result.getMessage(), "PAYMENT_FAILED");
            }

            return mapPayment(saved);
        }
    }

    @Transactional(readOnly = true)
    public PaymentDto getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        verifyPaymentAccess(payment);
        return mapPayment(payment);
    }

    @Transactional(readOnly = true)
    public String getPaymentStatus(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        verifyPaymentAccess(payment);
        return payment.getStatus().name();
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

    private void verifyPaymentAccess(Payment payment) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User customer = payment.getTransaction().getUser();
        User collector = payment.getTransaction().getCollector() != null ? payment.getTransaction().getCollector().getUser() : null;

        if (!customer.getId().equals(currentUserId) && (collector == null || !collector.getId().equals(currentUserId))) {
            throw new UnauthorizedException("Access denied: You are not authorized to view this payment.");
        }
    }

    public PaymentDto mapPayment(Payment p) {
        PaymentDto dto = new PaymentDto();
        dto.setId(p.getId());
        dto.setPaymentId(p.getPaymentId());
        if (p.getTransaction() != null) {
            dto.setTransactionId(p.getTransaction().getId());
            dto.setTransactionRef(p.getTransaction().getTransactionId());
        }
        dto.setAmount(p.getAmount());
        dto.setMethod(p.getMethod().name());
        dto.setStatus(p.getStatus().name());
        dto.setProviderReference(p.getProviderReference());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setCompletedAt(p.getCompletedAt());
        return dto;
    }

    public WalletDto mapWallet(Wallet w) {
        WalletDto dto = new WalletDto();
        dto.setId(w.getId());
        dto.setUserId(w.getUser().getId());
        dto.setBalance(w.getBalance());
        dto.setTotalEarned(w.getBalance());
        dto.setTotalWithdrawn(w.getBalance());
        dto.setUpdatedAt(w.getUpdatedAt());
        return dto;
    }
}
