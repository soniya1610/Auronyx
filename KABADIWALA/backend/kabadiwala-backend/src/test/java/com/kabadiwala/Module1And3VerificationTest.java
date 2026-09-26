package com.kabadiwala;

import com.kabadiwala.dto.AuthResponse;
import com.kabadiwala.dto.LoginRequest;
import com.kabadiwala.dto.RegisterRequest;
import com.kabadiwala.entity.*;
import com.kabadiwala.repository.*;
import com.kabadiwala.security.CustomUserDetails;
import com.kabadiwala.security.JwtService;
import com.kabadiwala.security.SecurityUtils;
import com.kabadiwala.service.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class Module1And3VerificationTest {

    private MockedStatic<SecurityUtils> mockedSecurityUtils;

    // Repositories
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private CollectorRepository collectorRepository;
    private RecyclerRepository recyclerRepository;
    private WalletRepository walletRepository;
    private NotificationRepository notificationRepository;
    private PointLedgerRepository pointLedgerRepository;
    private RewardRepository rewardRepository;
    private RedemptionRepository redemptionRepository;
    private FraudAlertRepository fraudAlertRepository;
    private PickupRepository pickupRepository;
    private TransactionRepository transactionRepository;
    private RecyclingRecordRepository recyclingRecordRepository;

    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;

    // Services
    private AuthService authService;
    private NotificationService notificationService;
    private PointService pointService;
    private GamificationService gamificationService;
    private RewardService rewardService;
    private FraudDetectionService fraudDetectionService;
    private AnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);

        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        collectorRepository = mock(CollectorRepository.class);
        recyclerRepository = mock(RecyclerRepository.class);
        walletRepository = mock(WalletRepository.class);
        notificationRepository = mock(NotificationRepository.class);
        pointLedgerRepository = mock(PointLedgerRepository.class);
        rewardRepository = mock(RewardRepository.class);
        redemptionRepository = mock(RedemptionRepository.class);
        fraudAlertRepository = mock(FraudAlertRepository.class);
        pickupRepository = mock(PickupRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        recyclingRecordRepository = mock(RecyclingRecordRepository.class);

        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        authenticationManager = mock(AuthenticationManager.class);

        authService = new AuthService(
                userRepository,
                roleRepository,
                collectorRepository,
                recyclerRepository,
                walletRepository,
                passwordEncoder,
                jwtService,
                authenticationManager
        );

        notificationService = new NotificationService(notificationRepository);
        pointService = new PointService(pointLedgerRepository);
        gamificationService = new GamificationService(pointLedgerRepository);
        rewardService = new RewardService(rewardRepository);
        fraudDetectionService = new FraudDetectionService(fraudAlertRepository, pickupRepository);
        analyticsService = new AnalyticsService(pickupRepository, transactionRepository, recyclingRecordRepository, collectorRepository);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtils.close();
    }

    @Test
    @DisplayName("Module 1 - Register new user provisions role, password hash, and wallet")
    void testUserRegistration() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Rahul Sharma");
        request.setEmail("rahul@example.com");
        request.setPassword("Secret123!");
        request.setPhone("9876543210");
        request.setRole("USER");

        when(userRepository.existsByEmail("rahul@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Secret123!")).thenReturn("encoded_hash");

        Role role = new Role("ROLE_USER", "ROLE_USER");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(101L);
            return u;
        });

        when(jwtService.generateToken(any(CustomUserDetails.class))).thenReturn("mock_jwt_token_123");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("mock_jwt_token_123", response.getToken());
        assertEquals("rahul@example.com", response.getEmail());
        assertTrue(response.getRoles().contains("ROLE_USER"));

        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    @DisplayName("Module 1 - Collector registration auto-provisions Collector profile")
    void testCollectorRegistration() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Collector Amit");
        request.setEmail("amit@collector.com");
        request.setPassword("Secret123!");
        request.setRole("COLLECTOR");

        when(userRepository.existsByEmail("amit@collector.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hash");

        Role role = new Role("ROLE_COLLECTOR", "ROLE_COLLECTOR");
        when(roleRepository.findByName("ROLE_COLLECTOR")).thenReturn(Optional.of(role));

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(201L);
            return u;
        });
        when(jwtService.generateToken(any())).thenReturn("col_token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        verify(collectorRepository, times(1)).save(any(Collector.class));
    }

    @Test
    @DisplayName("Module 1 - Notifications workflow: send, unread count, and mark as read")
    void testNotificationsWorkflow() {
        User user = new User();
        user.setId(50L);

        Notification notification = new Notification(user, "Pickup Scheduled", "Collector on way", "INFO");
        notification.setId(99L);
        notification.setIsRead(false);

        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(notificationRepository.countByUserIdAndIsReadFalse(50L)).thenReturn(1L);
        when(notificationRepository.findById(99L)).thenReturn(Optional.of(notification));

        Notification sent = notificationService.sendNotification(user, "Pickup Scheduled", "Collector on way", "INFO");
        assertNotNull(sent);

        long unread = notificationService.getUnreadCount(50L);
        assertEquals(1L, unread);

        notificationService.markAsRead(99L);
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Module 3 - Points ledger: earn points, calculate balance, and view history")
    void testPointsAndGamification() {
        User user = new User();
        user.setId(70L);

        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(70L);

        when(pointLedgerRepository.sumEarnedPointsByUserId(70L)).thenReturn(150);
        when(pointLedgerRepository.sumRedeemedPointsByUserId(70L)).thenReturn(50);

        Map<String, Integer> balance = pointService.getMyBalance();
        assertEquals(150, balance.get("earned"));
        assertEquals(50, balance.get("redeemed"));
        assertEquals(100, balance.get("balance"));

        // Gamification awards 10 points
        gamificationService.awardPickupCompletionPoints(user, 12L);
        verify(pointLedgerRepository, times(1)).save(any(PointLedger.class));
    }

    @Test
    @DisplayName("Module 3 - Fraud detection flags extreme weight claims")
    void testFraudDetectionExtremeWeight() {
        User citizen = new User();
        citizen.setId(88L);
        Pickup pickup = new Pickup();
        pickup.setId(555L);
        pickup.setUser(citizen);

        fraudDetectionService.analyzePickup(pickup, new BigDecimal("650.0")); // > 500kg threshold

        verify(fraudAlertRepository, times(1)).save(any(FraudAlert.class));
    }

    @Test
    @DisplayName("Module 3 - Admin summary aggregates platform metrics")
    void testAdminAnalyticsSummary() {
        when(pickupRepository.count()).thenReturn(120L);
        when(pickupRepository.countByStatus(Pickup.Status.COMPLETED)).thenReturn(100L);
        when(pickupRepository.countByStatus(Pickup.Status.REQUESTED)).thenReturn(15L);
        when(transactionRepository.sumAllFinalAmounts()).thenReturn(new BigDecimal("15400.00"));
        when(transactionRepository.sumAllActualWeights()).thenReturn(new BigDecimal("3200.5"));

        Map<String, Object> summary = analyticsService.getAdminSummary();

        assertNotNull(summary);
        assertEquals(120L, summary.get("totalPickups"));
        assertEquals(100L, summary.get("completedPickups"));
        assertEquals(new BigDecimal("15400.00"), summary.get("totalRevenueINR"));
        assertEquals(new BigDecimal("3200.5"), summary.get("totalWeightRecycledKg"));
    }
}
