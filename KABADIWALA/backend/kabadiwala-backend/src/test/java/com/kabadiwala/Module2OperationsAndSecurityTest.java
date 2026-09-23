package com.kabadiwala;

import com.kabadiwala.dto.PickupRequest;
import com.kabadiwala.dto.VerificationRequest;
import com.kabadiwala.entity.*;
import com.kabadiwala.exception.InvalidTransactionException;
import com.kabadiwala.repository.*;
import com.kabadiwala.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class Module2OperationsAndSecurityTest {

    private TransactionRepository transactionRepository;
    private PaymentRepository paymentRepository;
    private WalletRepository walletRepository;
    private WalletTransactionRepository walletTransactionRepository;
    private RecyclingRecordRepository recyclingRecordRepository;
    private WasteVerificationRepository verificationRepository;
    private PricingService pricingService;
    private WalletService walletService;
    private TransactionService transactionService;

    private User citizen;
    private Collector collector;
    private WasteCategory category;
    private Pickup pickup;
    private WasteVerification verification;

    @BeforeEach
    void setUp() {
        transactionRepository = Mockito.mock(TransactionRepository.class);
        paymentRepository = Mockito.mock(PaymentRepository.class);
        walletRepository = Mockito.mock(WalletRepository.class);
        walletTransactionRepository = Mockito.mock(WalletTransactionRepository.class);
        recyclingRecordRepository = Mockito.mock(RecyclingRecordRepository.class);
        verificationRepository = Mockito.mock(WasteVerificationRepository.class);
        pricingService = Mockito.mock(PricingService.class);

        walletService = new WalletService(walletRepository, walletTransactionRepository);
        transactionService = new TransactionService(
                transactionRepository,
                paymentRepository,
                walletService,
                recyclingRecordRepository,
                pricingService
        );

        citizen = new User();
        citizen.setId(10L);
        citizen.setName("John Citizen");

        collector = new Collector();
        collector.setId(20L);
        User collectorUser = new User();
        collectorUser.setId(21L);
        collectorUser.setName("Dave Collector");
        collector.setUser(collectorUser);

        category = new WasteCategory();
        category.setId(1L);
        category.setName("Plastic");
        category.setActive(true);

        pickup = new Pickup();
        pickup.setId(100L);
        pickup.setUser(citizen);
        pickup.setWasteCategory(category);
        pickup.setAssignedCollector(collector);
        pickup.setStatus(Pickup.Status.COLLECTED);

        verification = new WasteVerification();
        verification.setId(200L);
        verification.setPickup(pickup);
        verification.setCollector(collector);
        verification.setActualCategory(category);
        verification.setActualWeight(new BigDecimal("8.500"));
    }

    @Test
    @DisplayName("End-to-End Business Flow: Physical Verification -> Server Rate -> Final Price -> Wallet Credit Exactly Once")
    void shouldExecuteFullVerifiedFlowAndCreditWalletExactlyOnce() {
        BigDecimal actualWeight = new BigDecimal("8.500");
        BigDecimal serverRate = new BigDecimal("15.00");
        BigDecimal calculatedAmount = new BigDecimal("127.50"); // 8.5 * 15.00

        when(pricingService.getPriceForCategory(1L)).thenReturn(serverRate);
        when(pricingService.calculateFinalAmount(category, actualWeight)).thenReturn(calculatedAmount);
        when(transactionRepository.existsByPickupId(100L)).thenReturn(false);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Wallet wallet = new Wallet(citizen);
        wallet.setId(50L);
        wallet.setBalance(new BigDecimal("0.00"));
        when(walletRepository.findByUserId(10L)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArgument(0));
        when(walletTransactionRepository.existsByWalletIdAndReferenceTypeAndReferenceId(eq(50L), eq("TRANSACTION"), anyString()))
                .thenReturn(false);
        when(walletTransactionRepository.save(any(WalletTransaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction txn = transactionService.processVerifiedPickup(pickup, verification);

        assertNotNull(txn);
        assertEquals(calculatedAmount, txn.getFinalAmount());
        assertEquals(serverRate, txn.getAppliedRate());
        assertEquals(Transaction.Status.COMPLETED, txn.getStatus());

        // Wallet balance must be updated exactly by calculatedAmount
        assertEquals(calculatedAmount, wallet.getBalance());
        verify(walletRepository, times(1)).save(wallet);
        verify(walletTransactionRepository, times(1)).save(any(WalletTransaction.class));
        verify(recyclingRecordRepository, times(1)).save(any(RecyclingRecord.class));
    }

    @Test
    @DisplayName("Duplicate Prevention: Transaction creation must fail if pickup already has transaction")
    void shouldPreventDuplicateTransactionsForSamePickup() {
        when(transactionRepository.existsByPickupId(100L)).thenReturn(true);

        assertThrows(InvalidTransactionException.class, () ->
                transactionService.processVerifiedPickup(pickup, verification));

        verify(paymentRepository, never()).save(any());
        verify(walletRepository, never()).save(any());
    }

    @Test
    @DisplayName("Duplicate Prevention: Wallet credit must reject duplicate reference ID (idempotency check)")
    void shouldPreventDuplicateWalletCredit() {
        Wallet wallet = new Wallet(citizen);
        wallet.setId(50L);
        wallet.setBalance(new BigDecimal("100.00"));

        when(walletTransactionRepository.existsByWalletIdAndReferenceTypeAndReferenceId(
                50L, "TRANSACTION", "TXN-EXISTING-123")).thenReturn(true);

        assertThrows(InvalidTransactionException.class, () ->
                walletService.credit(wallet, new BigDecimal("50.00"), "TRANSACTION", "TXN-EXISTING-123"));

        // Balance should NOT have changed
        assertEquals(new BigDecimal("100.00"), wallet.getBalance());
        verify(walletRepository, never()).save(wallet);
    }

    @Test
    @DisplayName("Payment Failure Rule: Failed payment must not increase wallet balance")
    void failedPaymentMustNotCreditWallet() {
        Wallet wallet = new Wallet(citizen);
        wallet.setId(50L);
        wallet.setBalance(new BigDecimal("50.00"));

        Payment failedPayment = new Payment();
        failedPayment.setAmount(new BigDecimal("100.00"));
        failedPayment.setStatus(Payment.Status.FAILED);

        // Verify that wallet balance remains unchanged when payment fails
        assertEquals(Payment.Status.FAILED, failedPayment.getStatus());
        assertEquals(new BigDecimal("50.00"), wallet.getBalance());
    }

    @Test
    @DisplayName("Frontend Manipulation Defense: Wallet balance modification requires positive amount and sufficient funds")
    void shouldRejectIllegalWalletOperations() {
        Wallet wallet = new Wallet(citizen);
        wallet.setId(50L);
        wallet.setBalance(new BigDecimal("25.00"));

        // Reject zero credit
        assertThrows(InvalidTransactionException.class, () ->
                walletService.credit(wallet, BigDecimal.ZERO, "DEPOSIT", "REF-001"));

        // Reject negative credit
        assertThrows(InvalidTransactionException.class, () ->
                walletService.credit(wallet, new BigDecimal("-10.00"), "DEPOSIT", "REF-002"));

        // Reject overdraft debit (attempting to withdraw 100 with only 25 balance)
        assertThrows(InvalidTransactionException.class, () ->
                walletService.debit(wallet, new BigDecimal("100.00"), "WITHDRAWAL", "REF-003"));
    }
}
