package com.kabadiwala;

import com.kabadiwala.entity.*;
import com.kabadiwala.exception.InvalidTransactionException;
import com.kabadiwala.repository.*;
import com.kabadiwala.security.SecurityUtils;
import com.kabadiwala.service.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Module3RewardsEcosystemTest — Comprehensive Unit Tests for Module 3.
 *
 * Covers: Points, Rewards, Redemption, Referral, Gamification, Fraud Detection (rewards),
 *         Analytics (rewards dashboard).
 */
class Module3RewardsEcosystemTest {

    private MockedStatic<SecurityUtils> mockedSecurityUtils;

    // Repositories
    private PointLedgerRepository pointLedgerRepository;
    private RewardRepository rewardRepository;
    private RedemptionRepository redemptionRepository;
    private ReferralRepository referralRepository;
    private UserRepository userRepository;
    private BadgeRepository badgeRepository;
    private ChallengeRepository challengeRepository;
    private PickupRepository pickupRepository;
    private FraudAlertRepository fraudAlertRepository;
    private TransactionRepository transactionRepository;
    private RecyclingRecordRepository recyclingRecordRepository;
    private CollectorRepository collectorRepository;
    private WalletRepository walletRepository;
    private WalletTransactionRepository walletTransactionRepository;

    // Services
    private PointService pointService;
    private RewardService rewardService;
    private RedemptionService redemptionService;
    private ReferralService referralService;
    private GamificationService gamificationService;
    private FraudDetectionService fraudDetectionService;
    private AnalyticsService analyticsService;
    private WalletService walletService;

    private User testUser;

    @BeforeEach
    void setUp() {
        mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class);

        // Mock repositories
        pointLedgerRepository   = mock(PointLedgerRepository.class);
        rewardRepository        = mock(RewardRepository.class);
        redemptionRepository    = mock(RedemptionRepository.class);
        referralRepository      = mock(ReferralRepository.class);
        userRepository          = mock(UserRepository.class);
        badgeRepository         = mock(BadgeRepository.class);
        challengeRepository     = mock(ChallengeRepository.class);
        pickupRepository        = mock(PickupRepository.class);
        fraudAlertRepository    = mock(FraudAlertRepository.class);
        transactionRepository   = mock(TransactionRepository.class);
        recyclingRecordRepository = mock(RecyclingRecordRepository.class);
        collectorRepository     = mock(CollectorRepository.class);
        walletRepository        = mock(WalletRepository.class);
        walletTransactionRepository = mock(WalletTransactionRepository.class);

        // Wire services
        walletService       = new WalletService(walletRepository, walletTransactionRepository);
        pointService        = new PointService(pointLedgerRepository);
        rewardService       = new RewardService(rewardRepository);
        redemptionService   = new RedemptionService(redemptionRepository, rewardRepository,
                                                     pointLedgerRepository, walletService);
        referralService     = new ReferralService(referralRepository, userRepository, pointLedgerRepository);
        gamificationService = new GamificationService(pointLedgerRepository, badgeRepository,
                                                       challengeRepository, pickupRepository);
        fraudDetectionService = new FraudDetectionService(fraudAlertRepository, pickupRepository, redemptionRepository);
        analyticsService    = new AnalyticsService(pickupRepository, transactionRepository,
                                                    recyclingRecordRepository, collectorRepository,
                                                    pointLedgerRepository, redemptionRepository,
                                                    fraudAlertRepository, referralRepository);

        // Default test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@kabadiwala.com");
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtils.close();
    }

    // ==================== POINTS ====================

    @Test
    @DisplayName("Module 3 - Points: earn points saves a EARNED ledger entry")
    void testEarnPoints() {
        when(pointLedgerRepository.save(any(PointLedger.class))).thenAnswer(i -> i.getArgument(0));

        PointLedger ledger = pointService.earnPoints(testUser, 50, "Test earn", "REF-001");

        assertNotNull(ledger);
        assertEquals(50, ledger.getPoints());
        assertEquals(PointLedger.PointType.EARNED, ledger.getType());
        verify(pointLedgerRepository, times(1)).save(any(PointLedger.class));
    }

    @Test
    @DisplayName("Module 3 - Points: getMyBalance calculates earned minus redeemed correctly")
    void testGetMyBalance() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
        when(pointLedgerRepository.sumEarnedPointsByUserId(1L)).thenReturn(200);
        when(pointLedgerRepository.sumRedeemedPointsByUserId(1L)).thenReturn(75);

        Map<String, Integer> balance = pointService.getMyBalance();

        assertEquals(200, balance.get("earned"));
        assertEquals(75,  balance.get("redeemed"));
        assertEquals(125, balance.get("balance"));
    }

    @Test
    @DisplayName("Module 3 - Points: balance is zero when no transactions exist")
    void testGetMyBalanceEmpty() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(99L);
        when(pointLedgerRepository.sumEarnedPointsByUserId(99L)).thenReturn(null);
        when(pointLedgerRepository.sumRedeemedPointsByUserId(99L)).thenReturn(null);

        Map<String, Integer> balance = pointService.getMyBalance();

        assertEquals(0, balance.get("earned"));
        assertEquals(0, balance.get("redeemed"));
        assertEquals(0, balance.get("balance"));
    }

    // ==================== REWARDS ====================

    @Test
    @DisplayName("Module 3 - Rewards: getAllActiveRewards returns only active rewards")
    void testGetAllActiveRewards() {
        Reward r1 = buildReward(1L, "Cash ₹10", Reward.RewardType.CASH_BACK, 100, new BigDecimal("10"), true);
        Reward r2 = buildReward(2L, "Badge",    Reward.RewardType.BADGE,     200, null, true);
        when(rewardRepository.findByActiveTrue()).thenReturn(List.of(r1, r2));

        List<Reward> rewards = rewardService.getAllActiveRewards();

        assertEquals(2, rewards.size());
        verify(rewardRepository, times(1)).findByActiveTrue();
    }

    @Test
    @DisplayName("Module 3 - Rewards: toggleActive flips reward active state")
    void testToggleRewardActive() {
        Reward reward = buildReward(1L, "Cash ₹10", Reward.RewardType.CASH_BACK, 100, new BigDecimal("10"), true);
        when(rewardRepository.findById(1L)).thenReturn(Optional.of(reward));
        when(rewardRepository.save(any(Reward.class))).thenAnswer(i -> i.getArgument(0));

        Reward toggled = rewardService.toggleActive(1L);

        assertFalse(toggled.getActive());
        verify(rewardRepository).save(reward);
    }

    // ==================== REDEMPTION ====================

    @Test
    @DisplayName("Module 3 - Redemption: successful CASH_BACK redemption deducts points and credits wallet")
    void testSuccessfulCashBackRedemption() {
        Reward reward = buildReward(1L, "Cash ₹10", Reward.RewardType.CASH_BACK, 100, new BigDecimal("10"), true);

        mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
        when(rewardRepository.findById(1L)).thenReturn(Optional.of(reward));
        when(redemptionRepository.findByStatus(Redemption.RedemptionStatus.COMPLETED)).thenReturn(List.of());
        when(pointLedgerRepository.sumEarnedPointsByUserId(1L)).thenReturn(200);
        when(pointLedgerRepository.sumRedeemedPointsByUserId(1L)).thenReturn(0);
        when(pointLedgerRepository.save(any(PointLedger.class))).thenAnswer(i -> i.getArgument(0));

        Redemption savedRedemption = new Redemption();
        savedRedemption.setId(10L);
        savedRedemption.setUser(testUser);
        savedRedemption.setReward(reward);
        savedRedemption.setPointsUsed(100);
        savedRedemption.setCashValue(new BigDecimal("10"));
        savedRedemption.setStatus(Redemption.RedemptionStatus.COMPLETED);
        when(redemptionRepository.save(any(Redemption.class))).thenReturn(savedRedemption);

        // Wallet credit
        Wallet wallet = new Wallet(testUser);
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.ZERO);
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        WalletTransaction wt = new WalletTransaction(wallet, WalletTransaction.Type.CREDIT,
                new BigDecimal("10"), "REDEMPTION", "10");
        when(walletTransactionRepository.existsByWalletIdAndReferenceTypeAndReferenceId(any(), any(), any()))
                .thenReturn(false);
        when(walletTransactionRepository.save(any(WalletTransaction.class))).thenReturn(wt);

        Redemption result = redemptionService.redeemReward(1L);

        assertNotNull(result);
        assertEquals(Redemption.RedemptionStatus.COMPLETED, result.getStatus());
        // Points deduction entry saved
        verify(pointLedgerRepository, atLeast(1)).save(any(PointLedger.class));
        // Wallet credited
        verify(walletTransactionRepository, times(1)).save(any(WalletTransaction.class));
    }

    @Test
    @DisplayName("Module 3 - Redemption: insufficient points throws InvalidTransactionException")
    void testRedemptionInsufficientPoints() {
        Reward reward = buildReward(1L, "Cash ₹50", Reward.RewardType.CASH_BACK, 500, new BigDecimal("50"), true);

        mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
        when(rewardRepository.findById(1L)).thenReturn(Optional.of(reward));
        when(redemptionRepository.findByStatus(Redemption.RedemptionStatus.COMPLETED)).thenReturn(List.of());
        when(pointLedgerRepository.sumEarnedPointsByUserId(1L)).thenReturn(100); // only 100, need 500
        when(pointLedgerRepository.sumRedeemedPointsByUserId(1L)).thenReturn(0);

        assertThrows(InvalidTransactionException.class, () -> redemptionService.redeemReward(1L));
    }

    @Test
    @DisplayName("Module 3 - Redemption: inactive reward throws InvalidTransactionException")
    void testRedemptionInactiveReward() {
        Reward reward = buildReward(1L, "Old reward", Reward.RewardType.CASH_BACK, 100, null, false);

        mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
        when(rewardRepository.findById(1L)).thenReturn(Optional.of(reward));

        assertThrows(InvalidTransactionException.class, () -> redemptionService.redeemReward(1L));
    }

    // ==================== REFERRAL ====================

    @Test
    @DisplayName("Module 3 - Referral: generateReferralCode creates unique code and persists it")
    void testGenerateReferralCode() {
        User user = new User();
        user.setId(5L);
        user.setReferralCode(null);

        when(userRepository.existsByReferralCode(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        String code = referralService.generateReferralCode(user);

        assertNotNull(code);
        assertTrue(code.startsWith("KBD-"));
        assertEquals(code, user.getReferralCode());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Module 3 - Referral: generateReferralCode is idempotent for existing code")
    void testGenerateReferralCodeIdempotent() {
        testUser.setReferralCode("KBD-EXIST");

        String code = referralService.generateReferralCode(testUser);

        assertEquals("KBD-EXIST", code);
        verify(userRepository, never()).save(any()); // no new save
    }

    @Test
    @DisplayName("Module 3 - Referral: applyReferralCode awards bonus points to both users")
    void testApplyReferralCode() {
        User referrer = new User();
        referrer.setId(10L);
        referrer.setName("Referrer");
        referrer.setReferralCode("KBD-BONUS");

        User newUser = new User();
        newUser.setId(20L);
        newUser.setName("NewUser");

        when(referralRepository.existsByReferredId(20L)).thenReturn(false);
        when(userRepository.findByReferralCode("KBD-BONUS")).thenReturn(Optional.of(referrer));
        when(referralRepository.save(any(Referral.class))).thenAnswer(inv -> {
            Referral r = inv.getArgument(0);
            r.setId(99L);
            return r;
        });
        when(pointLedgerRepository.save(any(PointLedger.class))).thenAnswer(i -> i.getArgument(0));

        Optional<Referral> result = referralService.applyReferralCode(newUser, "KBD-BONUS");

        assertTrue(result.isPresent());
        assertEquals("KBD-BONUS", result.get().getReferralCode());
        // Both referrer and new user get points (2 saves)
        verify(pointLedgerRepository, times(2)).save(any(PointLedger.class));
    }

    @Test
    @DisplayName("Module 3 - Referral: invalid referral code returns empty Optional silently")
    void testApplyInvalidReferralCode() {
        User newUser = new User();
        newUser.setId(30L);

        when(referralRepository.existsByReferredId(30L)).thenReturn(false);
        when(userRepository.findByReferralCode("INVALID")).thenReturn(Optional.empty());

        Optional<Referral> result = referralService.applyReferralCode(newUser, "INVALID");

        assertTrue(result.isEmpty());
        verify(pointLedgerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Module 3 - Referral: double application is prevented")
    void testApplyReferralCodeDoubleApplicationPrevented() {
        User newUser = new User();
        newUser.setId(40L);

        when(referralRepository.existsByReferredId(40L)).thenReturn(true); // already applied

        Optional<Referral> result = referralService.applyReferralCode(newUser, "KBD-ANY");

        assertTrue(result.isEmpty());
        verify(userRepository, never()).findByReferralCode(any());
    }

    // ==================== GAMIFICATION ====================

    @Test
    @DisplayName("Module 3 - Gamification: awardPickupCompletionPoints saves 10 points per pickup")
    void testAwardPickupCompletionPoints() {
        when(pickupRepository.countByUserIdAndStatus(1L, Pickup.Status.COMPLETED)).thenReturn(5L);
        when(pointLedgerRepository.save(any(PointLedger.class))).thenAnswer(i -> i.getArgument(0));

        gamificationService.awardPickupCompletionPoints(testUser, 42L);

        verify(pointLedgerRepository, times(1)).save(argThat(p ->
                p.getPoints() == 10 && p.getType() == PointLedger.PointType.EARNED));
    }

    @Test
    @DisplayName("Module 3 - Gamification: 10-pickup milestone awards 100 bonus points")
    void testMilestone10PickupBonus() {
        when(pickupRepository.countByUserIdAndStatus(1L, Pickup.Status.COMPLETED)).thenReturn(10L);
        when(pointLedgerRepository.save(any(PointLedger.class))).thenAnswer(i -> i.getArgument(0));

        gamificationService.awardPickupCompletionPoints(testUser, 50L);

        // 2 saves: base points + milestone bonus
        verify(pointLedgerRepository, times(2)).save(any(PointLedger.class));
    }

    @Test
    @DisplayName("Module 3 - Gamification: getEarnedBadges returns badges for user's point level")
    void testGetEarnedBadges() {
        when(pointLedgerRepository.sumEarnedPointsByUserId(1L)).thenReturn(300);
        Badge greenBadge = new Badge();
        greenBadge.setName("Green Warrior");
        greenBadge.setPointsRequired(200);
        when(badgeRepository.findByPointsRequiredLessThanEqualOrderByPointsRequiredDesc(300))
                .thenReturn(List.of(greenBadge));

        List<Badge> badges = gamificationService.getEarnedBadges(1L);

        assertEquals(1, badges.size());
        assertEquals("Green Warrior", badges.get(0).getName());
    }

    @Test
    @DisplayName("Module 3 - Gamification: getActiveChallenges returns ACTIVE challenges only")
    void testGetActiveChallenges() {
        Challenge c = new Challenge();
        c.setTitle("Recycle 10kg");
        c.setStatus(Challenge.ChallengeStatus.ACTIVE);
        when(challengeRepository.findByStatus(Challenge.ChallengeStatus.ACTIVE)).thenReturn(List.of(c));

        List<Challenge> challenges = gamificationService.getActiveChallenges();

        assertEquals(1, challenges.size());
        assertEquals("Recycle 10kg", challenges.get(0).getTitle());
    }

    @Test
    @DisplayName("Module 3 - Gamification: adminAwardPoints prefixes description with [Admin]")
    void testAdminAwardPoints() {
        when(pointLedgerRepository.save(any(PointLedger.class))).thenAnswer(i -> i.getArgument(0));

        PointLedger ledger = gamificationService.adminAwardPoints(testUser, 200, "Exceptional recycler");

        assertEquals(200, ledger.getPoints());
        assertTrue(ledger.getDescription().startsWith("[Admin]"));
    }

    // ==================== FRAUD DETECTION (REWARDS) ====================

    @Test
    @DisplayName("Module 3 - Fraud: pickup extreme weight triggers HIGH fraud alert")
    void testFraudPickupExtremeWeight() {
        Pickup pickup = new Pickup();
        pickup.setId(1L);
        pickup.setUser(testUser);

        fraudDetectionService.analyzePickup(pickup, new BigDecimal("600"));

        verify(fraudAlertRepository, times(1)).save(argThat(a ->
                a.getSeverity() == FraudAlert.Severity.HIGH &&
                "EXTREME_WEIGHT".equals(a.getAlertType())));
    }

    @Test
    @DisplayName("Module 3 - Fraud: rapid reward redemptions (>3 in 1h) triggers MEDIUM fraud alert")
    void testFraudRapidRedemptions() {
        // Simulate 4 recent redemptions within the last hour
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        List<Redemption> recentRedemptions = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Redemption r = new Redemption();
            r.setId((long) i);
            r.setCreatedAt(now.minusMinutes(i * 5)); // within 1 hour
            recentRedemptions.add(r);
        }
        when(redemptionRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(recentRedemptions);

        fraudDetectionService.analyzeRedemption(testUser, 1L);

        verify(fraudAlertRepository, times(1)).save(argThat(a ->
                a.getSeverity() == FraudAlert.Severity.MEDIUM &&
                "RAPID_REDEMPTIONS".equals(a.getAlertType())));
    }

    @Test
    @DisplayName("Module 3 - Fraud: normal redemption frequency does NOT trigger fraud alert")
    void testFraudNormalRedemptionFrequency() {
        when(redemptionRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of());

        fraudDetectionService.analyzeRedemption(testUser, 1L);

        verify(fraudAlertRepository, never()).save(any());
    }

    // ==================== ANALYTICS ====================

    @Test
    @DisplayName("Module 3 - Analytics: admin summary includes rewards and referral metrics")
    void testAdminSummaryIncludesModule3Metrics() {
        when(pickupRepository.count()).thenReturn(50L);
        when(pickupRepository.countByStatus(Pickup.Status.COMPLETED)).thenReturn(40L);
        when(pickupRepository.countByStatus(Pickup.Status.REQUESTED)).thenReturn(8L);
        when(transactionRepository.sumAllFinalAmounts()).thenReturn(new BigDecimal("5000.00"));
        when(transactionRepository.sumAllActualWeights()).thenReturn(new BigDecimal("800.0"));
        when(fraudAlertRepository.countByStatus(FraudAlert.AlertStatus.OPEN)).thenReturn(3L);
        when(redemptionRepository.count()).thenReturn(25L);
        when(redemptionRepository.findByStatus(Redemption.RedemptionStatus.PENDING)).thenReturn(List.of());
        when(referralRepository.count()).thenReturn(12L);

        Map<String, Object> summary = analyticsService.getAdminSummary();

        assertNotNull(summary);
        assertEquals(50L, summary.get("totalPickups"));
        assertEquals(3L,  summary.get("openFraudAlerts"));
        assertEquals(25L, summary.get("totalRedemptions"));
        assertEquals(12L, summary.get("totalReferrals"));
    }

    @Test
    @DisplayName("Module 3 - Analytics: getRewardsAnalytics returns ecosystem metrics")
    void testGetRewardsAnalytics() {
        when(pointLedgerRepository.count()).thenReturn(100L);
        when(redemptionRepository.count()).thenReturn(30L);
        when(redemptionRepository.findByStatus(Redemption.RedemptionStatus.COMPLETED))
                .thenReturn(List.of(new Redemption()));
        when(redemptionRepository.findByStatus(Redemption.RedemptionStatus.REJECTED))
                .thenReturn(List.of());
        when(referralRepository.count()).thenReturn(15L);

        Map<String, Object> analytics = analyticsService.getRewardsAnalytics();

        assertNotNull(analytics);
        assertEquals(100L, analytics.get("totalPointLedgerEntries"));
        assertEquals(30L,  analytics.get("totalRedemptions"));
        assertEquals(15L,  analytics.get("totalReferrals"));
    }

    @Test
    @DisplayName("Module 3 - Analytics: user stats include points balance and referral count")
    void testUserStatsIncludePointsAndReferrals() {
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
        when(pickupRepository.countByUserId(1L)).thenReturn(10L);
        when(pickupRepository.countByUserIdAndStatus(1L, Pickup.Status.COMPLETED)).thenReturn(8L);
        when(pickupRepository.countByUserIdAndStatus(1L, Pickup.Status.CANCELLED)).thenReturn(1L);
        when(transactionRepository.sumFinalAmountByUserId(1L)).thenReturn(new BigDecimal("500"));
        when(transactionRepository.sumActualWeightByUserId(1L)).thenReturn(new BigDecimal("50"));
        when(pointLedgerRepository.sumEarnedPointsByUserId(1L)).thenReturn(150);
        when(pointLedgerRepository.sumRedeemedPointsByUserId(1L)).thenReturn(50);
        when(referralRepository.countByReferrerId(1L)).thenReturn(3L);

        Map<String, Object> stats = analyticsService.getUserStats();

        assertEquals(150, stats.get("pointsEarned"));
        assertEquals(50,  stats.get("pointsRedeemed"));
        assertEquals(100, stats.get("pointsBalance"));
        assertEquals(3L,  stats.get("totalReferrals"));
    }

    // ==================== EPR Service Smoke Test ====================

    @Test
    @DisplayName("Module 3 - EPR: smoke test verifies EPRService compiles and service layer functional")
    void testEPRServiceSmoke() {
        // EPR is already implemented — just verify the existing service works structurally
        EPRRecordRepository eprRepo = mock(EPRRecordRepository.class);
        RecyclerRepository recyclerRepo = mock(RecyclerRepository.class);
        EPRService eprService = new EPRService(eprRepo, recyclerRepo);
        when(eprRepo.findAll()).thenReturn(List.of());
        List<EPRRecord> records = eprService.getAllEPRRecords();
        assertNotNull(records);
    }

    // ---- Helpers ----

    private Reward buildReward(Long id, String title, Reward.RewardType type,
                                int pointsCost, BigDecimal cashValue, boolean active) {
        Reward r = new Reward();
        r.setId(id);
        r.setTitle(title);
        r.setType(type);
        r.setPointsCost(pointsCost);
        r.setCashValue(cashValue);
        r.setActive(active);
        return r;
    }
}
