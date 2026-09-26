package com.kabadiwala;

import com.kabadiwala.dto.*;
import com.kabadiwala.entity.*;
import com.kabadiwala.exception.InvalidTransactionException;
import com.kabadiwala.exception.UnauthorizedException;
import com.kabadiwala.repository.*;
import com.kabadiwala.security.SecurityUtils;
import com.kabadiwala.service.*;
import com.kabadiwala.service.payment.MockPaymentProvider;
import com.kabadiwala.service.payment.PaymentProvider;
import com.kabadiwala.service.payment.PaymentResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class Module2ComprehensiveOperationsTest {

    private MockedStatic<SecurityUtils> mockedSecurityUtils;

    private CollectorRepository collectorRepository;
    private PickupRepository pickupRepository;
    private PickupStatusRepository statusRepository;
    private NotificationService notificationService;
    private PickupService pickupService;
    private CollectorOperationsService collectorOperationsService;

    private PaymentRepository paymentRepository;
    private TransactionRepository transactionRepository;
    private WalletRepository walletRepository;
    private WalletTransactionRepository walletTransactionRepository;
    private WalletService walletService;
    private TransactionService transactionService;
    private PaymentProvider paymentProvider;
    private PaymentService paymentService;

    private RecyclingRecordRepository recyclingRecordRepository;
    private RecyclerRepository recyclerRepository;
    private RecyclingService recyclingService;

    private User citizen;
    private User collectorUser;
    private Collector collector;
    private User recyclerUser;
    private Recycler recycler;

    @BeforeEach
    void setUp() {
        mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);

        citizen = new User();
        citizen.setId(10L);
        citizen.setName("Citizen Alice");
        citizen.setEmail("alice@kabadiwala.com");

        collectorUser = new User();
        collectorUser.setId(20L);
        collectorUser.setName("Collector Bob");
        collectorUser.setEmail("bob@kabadiwala.com");

        collector = new Collector();
        collector.setId(100L);
        collector.setUser(collectorUser);
        collector.setLatitude(28.6139);
        collector.setLongitude(77.2090); // New Delhi
        collector.setServiceArea("Delhi");

        recyclerUser = new User();
        recyclerUser.setId(30L);
        recyclerUser.setName("Recycler Charlie");

        recycler = new Recycler();
        recycler.setId(200L);
        recycler.setUser(recyclerUser);
        recycler.setBusinessName("Green Earth Recyclers");

        // Collector operations mocks
        collectorRepository = Mockito.mock(CollectorRepository.class);
        pickupRepository = Mockito.mock(PickupRepository.class);
        statusRepository = Mockito.mock(PickupStatusRepository.class);
        notificationService = Mockito.mock(NotificationService.class);
        pickupService = Mockito.mock(PickupService.class);
        collectorOperationsService = new CollectorOperationsService(
                collectorRepository, pickupRepository, statusRepository, notificationService, pickupService);

        // Payment and wallet mocks
        paymentRepository = Mockito.mock(PaymentRepository.class);
        transactionRepository = Mockito.mock(TransactionRepository.class);
        walletRepository = Mockito.mock(WalletRepository.class);
        walletTransactionRepository = Mockito.mock(WalletTransactionRepository.class);
        walletService = new WalletService(walletRepository, walletTransactionRepository);
        transactionService = Mockito.mock(TransactionService.class);
        paymentProvider = Mockito.mock(PaymentProvider.class);
        paymentService = new PaymentService(
                walletService, transactionService, paymentRepository, transactionRepository, paymentProvider, notificationService);

        // Recycling mocks
        recyclingRecordRepository = Mockito.mock(RecyclingRecordRepository.class);
        recyclerRepository = Mockito.mock(RecyclerRepository.class);
        recyclingService = new RecyclingService(recyclingRecordRepository, recyclerRepository, pickupRepository);
    }

    @AfterEach
    void tearDown() {
        if (mockedSecurityUtils != null) {
            mockedSecurityUtils.close();
        }
    }

    @Test
    @DisplayName("Geospatial Logic: Nearby pickups calculated via Haversine, filtered by radius, and sorted")
    void testNearbyPickupsGeospatialFiltering() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(20L);
        when(collectorRepository.findByUserId(20L)).thenReturn(Optional.of(collector));

        // Pickup 1: ~0.68 km away (Connaught Place)
        Pickup p1 = new Pickup();
        p1.setId(1L);
        p1.setStatus(Pickup.Status.REQUESTED);
        p1.setLatitude(28.6200);
        p1.setLongitude(77.2100);

        // Pickup 2: ~14 km away (North Delhi)
        Pickup p2 = new Pickup();
        p2.setId(2L);
        p2.setStatus(Pickup.Status.REQUESTED);
        p2.setLatitude(28.7041);
        p2.setLongitude(77.1025);

        // Pickup 3: ~1150 km away (Mumbai) - Should be excluded by 25km radius
        Pickup p3 = new Pickup();
        p3.setId(3L);
        p3.setStatus(Pickup.Status.REQUESTED);
        p3.setLatitude(19.0760);
        p3.setLongitude(72.8777);

        when(pickupRepository.findAll()).thenReturn(List.of(p1, p2, p3));

        PickupDto dto1 = new PickupDto(); dto1.setId(1L);
        PickupDto dto2 = new PickupDto(); dto2.setId(2L);
        PickupDto dto3 = new PickupDto(); dto3.setId(3L);
        when(pickupService.mapToDto(p1)).thenReturn(dto1);
        when(pickupService.mapToDto(p2)).thenReturn(dto2);
        when(pickupService.mapToDto(p3)).thenReturn(dto3);

        List<PickupDto> nearby = collectorOperationsService.getNearbyPickups(null, null, 25.0);

        assertEquals(2, nearby.size(), "Only pickups within 25km must be included");
        assertEquals(1L, nearby.get(0).getId(), "Nearest pickup must be first");
        assertEquals(2L, nearby.get(1).getId());
        assertTrue(nearby.get(0).getDistanceKm() < nearby.get(1).getDistanceKm(), "List must be sorted ascending by distance");
        assertTrue(nearby.get(0).getDistanceKm() < 1.0, "Pickup 1 should be under 1 km");
    }

    @Test
    @DisplayName("Security: Payment MUST use backend transaction amount and ignore client-supplied amount")
    void testPaymentIgnoresClientPriceTampering() {
        Transaction txn = new Transaction();
        txn.setId(500L);
        txn.setTransactionId("TXN-VALID-123");
        txn.setUser(citizen);
        txn.setFinalAmount(new BigDecimal("150.00")); // Authoritative backend amount

        when(transactionRepository.findByPickupId(10L)).thenReturn(Optional.of(txn));
        when(paymentRepository.findByTransactionIdOrderByCreatedAtDesc(500L)).thenReturn(new ArrayList<>());
        when(paymentProvider.processPayment(eq("TXN-VALID-123"), eq(new BigDecimal("150.00")), any()))
                .thenReturn(new PaymentResult(true, "MOCK-REF-1", "Success", new BigDecimal("150.00")));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        Wallet wallet = new Wallet(citizen);
        wallet.setId(88L);
        wallet.setBalance(BigDecimal.ZERO);
        when(walletRepository.findByUserId(10L)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArgument(0));
        when(walletTransactionRepository.save(any(WalletTransaction.class))).thenAnswer(i -> i.getArgument(0));

        // Client attempts to tamper price by passing ₹999,999.00
        PaymentRequest attackRequest = new PaymentRequest();
        attackRequest.setPickupId(10L);
        attackRequest.setAmount(new BigDecimal("999999.00"));
        attackRequest.setPaymentMethod("WALLET");

        PaymentDto paymentDto = paymentService.processPayment(attackRequest);

        assertNotNull(paymentDto);
        assertEquals(new BigDecimal("150.00"), paymentDto.getAmount(), "Amount must be ₹150.00 from backend transaction");
        assertEquals("SUCCESS", paymentDto.getStatus());
        assertEquals(new BigDecimal("150.00"), wallet.getBalance(), "Wallet credited only ₹150.00, client tampering ignored");
    }

    @Test
    @DisplayName("Security: Payment failure MUST NOT credit wallet")
    void testPaymentFailureDoesNotCreditWallet() {
        Transaction txn = new Transaction();
        txn.setId(501L);
        txn.setTransactionId("TXN-FAIL-456");
        txn.setUser(citizen);
        txn.setFinalAmount(new BigDecimal("200.00"));

        when(transactionRepository.findByPickupId(11L)).thenReturn(Optional.of(txn));
        when(paymentRepository.findByTransactionIdOrderByCreatedAtDesc(501L)).thenReturn(new ArrayList<>());
        when(paymentProvider.processPayment(any(), any(), any()))
                .thenReturn(new PaymentResult(false, null, "Payment gateway timeout", new BigDecimal("200.00")));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        Wallet wallet = new Wallet(citizen);
        wallet.setId(88L);
        wallet.setBalance(new BigDecimal("50.00"));
        when(walletRepository.findByUserId(10L)).thenReturn(Optional.of(wallet));

        PaymentRequest request = new PaymentRequest();
        request.setPickupId(11L);
        request.setAmount(new BigDecimal("200.00"));

        PaymentDto result = paymentService.processPayment(request);

        assertEquals("FAILED", result.getStatus());
        assertEquals(new BigDecimal("50.00"), wallet.getBalance(), "Wallet balance must remain unchanged on payment failure");
        verify(walletRepository, never()).save(wallet);
        verify(walletTransactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Idempotency: Duplicate payment for already-paid transaction is rejected")
    void testDuplicatePaymentRejected() {
        Transaction txn = new Transaction();
        txn.setId(502L);
        txn.setTransactionId("TXN-PAID-789");
        txn.setFinalAmount(new BigDecimal("100.00"));

        Payment alreadySuccess = new Payment();
        alreadySuccess.setStatus(Payment.Status.SUCCESS);

        when(transactionRepository.findByPickupId(12L)).thenReturn(Optional.of(txn));
        when(paymentRepository.findByTransactionIdOrderByCreatedAtDesc(502L)).thenReturn(List.of(alreadySuccess));

        PaymentRequest req = new PaymentRequest();
        req.setPickupId(12L);

        assertThrows(InvalidTransactionException.class, () -> paymentService.processPayment(req));
    }

    @Test
    @DisplayName("Wallet Security: Accessing another user's wallet transaction throws UnauthorizedException")
    void testWalletTransactionSecurityAccessControl() {
        User otherUser = new User();
        otherUser.setId(999L);
        Wallet otherWallet = new Wallet(otherUser);
        otherWallet.setId(333L);

        WalletTransaction wt = new WalletTransaction(otherWallet, WalletTransaction.Type.CREDIT,
                new BigDecimal("50.00"), "TRANSACTION", "TXN-999");
        wt.setId(777L);

        when(walletTransactionRepository.findById(777L)).thenReturn(Optional.of(wt));

        // Alice (ID: 10L) attempts to access transaction of user 999L
        assertThrows(UnauthorizedException.class, () ->
                walletService.getWalletTransactionById(777L, 10L));
    }

    @Test
    @DisplayName("Recycling Lifecycle: Full timeline stages generated deterministically with valid flags")
    void testRecyclingTimelineStages() {
        WasteCategory plasticCat = new WasteCategory();
        plasticCat.setId(1L);
        plasticCat.setName("Plastic");

        RecyclingRecord rr = new RecyclingRecord();
        rr.setId(99L);
        rr.setStatus(RecyclingRecord.Status.TRANSPORT);
        rr.setWasteCategory(plasticCat);
        rr.setWeight(new BigDecimal("10.000"));
        rr.setCreatedAt(LocalDateTime.now().minusHours(3));
        rr.setUpdatedAt(LocalDateTime.now().minusHours(1));

        when(recyclingRecordRepository.findById(99L)).thenReturn(Optional.of(rr));

        RecyclingTimelineDto timeline = recyclingService.getTimelineByRecordId(99L);

        assertNotNull(timeline);
        assertEquals(7, timeline.getStages().size(), "Must generate all 7 standard lifecycle stages");
        assertEquals("TRANSPORT", timeline.getCurrentStatus());

        // COLLECTED, SORTED, AGGREGATED, TRANSPORT must be completed
        assertTrue(timeline.getStages().get(0).isCompleted(), "COLLECTED stage must be completed");
        assertTrue(timeline.getStages().get(1).isCompleted(), "SORTED stage must be completed");
        assertTrue(timeline.getStages().get(2).isCompleted(), "AGGREGATED stage must be completed");
        assertTrue(timeline.getStages().get(3).isCompleted(), "TRANSPORT stage must be completed");
        assertTrue(timeline.getStages().get(3).isCurrent(), "TRANSPORT stage must be marked current");

        // RECEIVED, PROCESSING, RECYCLED must NOT be completed
        assertFalse(timeline.getStages().get(4).isCompleted(), "RECEIVED must be pending");
        assertFalse(timeline.getStages().get(5).isCompleted(), "PROCESSING must be pending");
        assertFalse(timeline.getStages().get(6).isCompleted(), "RECYCLED must be pending");
    }

    @Test
    @DisplayName("Collector Operations: Rejection returns pickup to REQUESTED unassigned status")
    void testCollectorRejectPickup() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(20L);
        mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(collectorUser);
        when(collectorRepository.findByUserId(20L)).thenReturn(Optional.of(collector));

        Pickup pickup = new Pickup();
        pickup.setId(55L);
        pickup.setStatus(Pickup.Status.ACCEPTED);
        pickup.setAssignedCollector(collector);
        pickup.setUser(citizen);

        when(pickupRepository.findById(55L)).thenReturn(Optional.of(pickup));
        when(pickupRepository.save(any(Pickup.class))).thenAnswer(i -> i.getArgument(0));

        PickupDto dto = new PickupDto();
        dto.setId(55L);
        dto.setStatus("REQUESTED");
        when(pickupService.mapToDto(any(Pickup.class))).thenReturn(dto);

        PickupDto result = collectorOperationsService.rejectPickup(55L, "Collector vehicle breakdown");

        assertEquals(Pickup.Status.REQUESTED, pickup.getStatus());
        assertNull(pickup.getAssignedCollector());
        verify(statusRepository, times(1)).save(any(PickupStatusHistory.class));
        verify(notificationService, times(1)).sendNotification(eq(citizen), anyString(), anyString(), eq("PICKUP_REOPENED"));
    }
}
