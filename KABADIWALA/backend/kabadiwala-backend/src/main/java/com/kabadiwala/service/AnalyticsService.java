package com.kabadiwala.service;

import com.kabadiwala.entity.Collector;
import com.kabadiwala.entity.Pickup;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.*;
import com.kabadiwala.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Analytics Service — Modules 2 & 3.
 * Provides statistics for users, collectors, admin dashboard, and rewards ecosystem.
 */
@Service
public class AnalyticsService {

    private final PickupRepository pickupRepository;
    private final TransactionRepository transactionRepository;
    private final RecyclingRecordRepository recyclingRecordRepository;
    private final CollectorRepository collectorRepository;
    private final PointLedgerRepository pointLedgerRepository;
    private final RedemptionRepository redemptionRepository;
    private final FraudAlertRepository fraudAlertRepository;
    private final ReferralRepository referralRepository;

    public AnalyticsService(PickupRepository pickupRepository,
                             TransactionRepository transactionRepository,
                             RecyclingRecordRepository recyclingRecordRepository,
                             CollectorRepository collectorRepository,
                             PointLedgerRepository pointLedgerRepository,
                             RedemptionRepository redemptionRepository,
                             FraudAlertRepository fraudAlertRepository,
                             ReferralRepository referralRepository) {
        this.pickupRepository        = pickupRepository;
        this.transactionRepository   = transactionRepository;
        this.recyclingRecordRepository = recyclingRecordRepository;
        this.collectorRepository     = collectorRepository;
        this.pointLedgerRepository   = pointLedgerRepository;
        this.redemptionRepository    = redemptionRepository;
        this.fraudAlertRepository    = fraudAlertRepository;
        this.referralRepository      = referralRepository;
    }

    /**
     * Per-user statistics: total pickups, earnings, weight, points balance.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getUserStats() {
        Long userId = SecurityUtils.getCurrentUserId();
        Map<String, Object> stats = new HashMap<>();

        long totalPickups     = pickupRepository.countByUserId(userId);
        long completedPickups = pickupRepository.countByUserIdAndStatus(userId, Pickup.Status.COMPLETED);
        BigDecimal totalEarnings = transactionRepository.sumFinalAmountByUserId(userId);
        BigDecimal totalWeight   = transactionRepository.sumActualWeightByUserId(userId);

        Integer earned   = pointLedgerRepository.sumEarnedPointsByUserId(userId);
        Integer redeemed = pointLedgerRepository.sumRedeemedPointsByUserId(userId);
        int pointBalance = (earned != null ? earned : 0) - (redeemed != null ? redeemed : 0);

        stats.put("totalPickups",       totalPickups);
        stats.put("completedPickups",   completedPickups);
        stats.put("cancelledPickups",   pickupRepository.countByUserIdAndStatus(userId, Pickup.Status.CANCELLED));
        stats.put("totalEarnings",      totalEarnings != null ? totalEarnings : BigDecimal.ZERO);
        stats.put("totalWeightKg",      totalWeight   != null ? totalWeight   : BigDecimal.ZERO);
        stats.put("pointsEarned",       earned   != null ? earned   : 0);
        stats.put("pointsRedeemed",     redeemed != null ? redeemed : 0);
        stats.put("pointsBalance",      pointBalance);
        stats.put("totalReferrals",     referralRepository.countByReferrerId(userId));
        return stats;
    }

    /**
     * Collector-level stats: pickups handled, total weight, earnings.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getCollectorStats() {
        Long userId = SecurityUtils.getCurrentUserId();
        Collector collector = collectorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Collector profile", "userId", userId));

        Map<String, Object> stats = new HashMap<>();
        long handled    = pickupRepository.countByAssignedCollectorId(collector.getId());
        BigDecimal weight = transactionRepository.sumActualWeightByCollectorId(collector.getId());

        stats.put("pickupsHandled",          handled);
        stats.put("totalWeightCollectedKg",  weight != null ? weight : BigDecimal.ZERO);
        stats.put("serviceArea",             collector.getServiceArea());
        stats.put("vehicleNumber",           collector.getVehicleNumber());
        return stats;
    }

    /**
     * Admin-level platform summary: pickups, revenue, weight, rewards ecosystem metrics, fraud.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAdminSummary() {
        Map<String, Object> stats = new HashMap<>();

        long totalPickups     = pickupRepository.count();
        long completedPickups = pickupRepository.countByStatus(Pickup.Status.COMPLETED);
        BigDecimal totalRevenue = transactionRepository.sumAllFinalAmounts();
        BigDecimal totalWeight  = transactionRepository.sumAllActualWeights();

        // Fraud metrics
        long openFraudAlerts = fraudAlertRepository.countByStatus(
                com.kabadiwala.entity.FraudAlert.AlertStatus.OPEN);

        // Rewards ecosystem metrics
        long totalRedemptions = redemptionRepository.count();
        long pendingRedemptions = redemptionRepository.findByStatus(
                com.kabadiwala.entity.Redemption.RedemptionStatus.PENDING).size();

        stats.put("totalPickups",        totalPickups);
        stats.put("completedPickups",    completedPickups);
        stats.put("pendingPickups",      pickupRepository.countByStatus(Pickup.Status.REQUESTED));
        stats.put("totalRevenueINR",     totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        stats.put("totalWeightRecycledKg", totalWeight != null ? totalWeight : BigDecimal.ZERO);
        stats.put("openFraudAlerts",     openFraudAlerts);
        stats.put("totalRedemptions",    totalRedemptions);
        stats.put("pendingRedemptions",  pendingRedemptions);
        stats.put("totalReferrals",      referralRepository.count());
        return stats;
    }

    /**
     * Admin: rewards ecosystem analytics — total points issued, redeemed, net outstanding.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getRewardsAnalytics() {
        Map<String, Object> stats = new HashMap<>();
        // Platform-wide point sums using queries per user are expensive; use ledger counts
        long totalLedgerEntries  = pointLedgerRepository.count();
        long totalRedemptions    = redemptionRepository.count();
        long completedRedemptions = redemptionRepository.findByStatus(
                com.kabadiwala.entity.Redemption.RedemptionStatus.COMPLETED).size();
        long rejectedRedemptions  = redemptionRepository.findByStatus(
                com.kabadiwala.entity.Redemption.RedemptionStatus.REJECTED).size();
        long totalReferrals = referralRepository.count();

        stats.put("totalPointLedgerEntries", totalLedgerEntries);
        stats.put("totalRedemptions",        totalRedemptions);
        stats.put("completedRedemptions",    completedRedemptions);
        stats.put("rejectedRedemptions",     rejectedRedemptions);
        stats.put("totalReferrals",          totalReferrals);
        return stats;
    }
}
