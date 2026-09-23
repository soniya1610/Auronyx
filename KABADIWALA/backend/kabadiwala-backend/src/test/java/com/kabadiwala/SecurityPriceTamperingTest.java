package com.kabadiwala;

import com.kabadiwala.entity.*;
import com.kabadiwala.repository.*;
import com.kabadiwala.service.PricingService;
import com.kabadiwala.service.TransactionService;
import com.kabadiwala.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SecurityPriceTamperingTest {

    private TransactionRepository transactionRepository;
    private PaymentRepository paymentRepository;
    private WalletService walletService;
    private RecyclingRecordRepository recyclingRecordRepository;
    private PricingService pricingService;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionRepository = Mockito.mock(TransactionRepository.class);
        paymentRepository = Mockito.mock(PaymentRepository.class);
        walletService = Mockito.mock(WalletService.class);
        recyclingRecordRepository = Mockito.mock(RecyclingRecordRepository.class);
        pricingService = Mockito.mock(PricingService.class);

        transactionService = new TransactionService(
                transactionRepository,
                paymentRepository,
                walletService,
                recyclingRecordRepository,
                pricingService
        );
    }

    @Test
    @DisplayName("Transaction final price must strictly use backend pricing engine, ignoring any client inputs")
    void shouldStrictlyEnforceBackendPricingCalculation() {
        User user = new User();
        user.setId(5L);
        user.setName("Citizen Test");

        WasteCategory category = new WasteCategory();
        category.setId(2L);
        category.setName("Metal");

        Collector collector = new Collector();
        collector.setId(3L);
        collector.setUser(new User());

        Pickup pickup = new Pickup();
        pickup.setId(101L);
        pickup.setUser(user);
        pickup.setWasteCategory(category);
        pickup.setEstimatedWeight(new BigDecimal("10.000")); // User originally estimated 10kg

        // Physical verification: Collector verifies actual weight is 5.450 kg
        WasteVerification verification = new WasteVerification();
        verification.setPickup(pickup);
        verification.setCollector(collector);
        verification.setActualCategory(category);
        verification.setActualWeight(new BigDecimal("5.450"));

        // Server-configured rate: ₹35.00/kg
        BigDecimal configuredRate = new BigDecimal("35.00");
        BigDecimal calculatedFinal = new BigDecimal("190.75"); // 5.450 * 35.00

        when(transactionRepository.existsByPickupId(101L)).thenReturn(false);
        when(pricingService.getPriceForCategory(2L)).thenReturn(configuredRate);
        when(pricingService.calculateFinalAmount(category, new BigDecimal("5.450"))).thenReturn(calculatedFinal);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet mockWallet = new Wallet(user);
        when(walletService.getOrCreateWallet(user)).thenReturn(mockWallet);

        Transaction result = transactionService.processVerifiedPickup(pickup, verification);

        assertNotNull(result);
        assertEquals(configuredRate, result.getAppliedRate());
        assertEquals(calculatedFinal, result.getFinalAmount());
        assertEquals(new BigDecimal("5.450"), result.getActualWeight());

        // Verify that wallet was credited with strictly server-computed amount
        verify(walletService, times(1)).credit(eq(mockWallet), eq(calculatedFinal), eq("TRANSACTION"), anyString());
    }
}
