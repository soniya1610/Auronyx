package com.kabadiwala.service;

import com.kabadiwala.entity.Collector;
import com.kabadiwala.entity.User;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.CollectorRepository;
import com.kabadiwala.repository.PickupRepository;
import com.kabadiwala.repository.RecyclingRecordRepository;
import com.kabadiwala.repository.TransactionRepository;
import com.kabadiwala.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Analytics Service — Module 2.
 * Provides statistics for users, collectors, and admin dashboard.
 */
@Service
public class AnalyticsService {

    private final PickupRepository pickupRepository;
    private final TransactionRepository transactionRepository;
    private final RecyclingRecordRepository recyclingRecordRepository;
    private final CollectorRepository collectorRepository;

    public AnalyticsService(PickupRepository pickupRepository,
                             TransactionRepository transactionRepository,
                             RecyclingRecordRepository recyclingRecordRepository,
                             CollectorRepository collectorRepository) {
        this.pickupRepository = pickupRepository;
        this.transactionRepository = transactionRepository;
        this.recyclingRecordRepository = recyclingRecordRepository;
        this.collectorRepository = collectorRepository;
    }

    /**
     * Returns per-user statistics: total pickups, total earnings, total weight.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getUserStats() {
        Long userId = SecurityUtils.getCurrentUserId();
        Map<String, Object> stats = new HashMap<>();

        long totalPickups = pickupRepository.countByUserId(userId);
        long completedPickups = pickupRepository.countByUserIdAndStatus(userId,
                com.kabadiwala.entity.Pickup.Status.COMPLETED);
        BigDecimal totalEarnings = transactionRepository.sumFinalAmountByUserId(userId);
        BigDecimal totalWeight = transactionRepository.sumActualWeightByUserId(userId);

        stats.put("totalPickups", totalPickups);
        stats.put("completedPickups", completedPickups);
        stats.put("cancelledPickups", pickupRepository.countByUserIdAndStatus(userId,
                com.kabadiwala.entity.Pickup.Status.CANCELLED));
        stats.put("totalEarnings", totalWeight != null ? totalEarnings : BigDecimal.ZERO);
        stats.put("totalWeightKg", totalWeight != null ? totalWeight : BigDecimal.ZERO);
        return stats;
    }

    /**
     * Returns collector-level stats: pickups handled, total weight, earnings.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getCollectorStats() {
        Long userId = SecurityUtils.getCurrentUserId();
        Collector collector = collectorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Collector profile", "userId", userId));

        Map<String, Object> stats = new HashMap<>();
        long handled = pickupRepository.countByAssignedCollectorId(collector.getId());
        BigDecimal weight = transactionRepository.sumActualWeightByCollectorId(collector.getId());

        stats.put("pickupsHandled", handled);
        stats.put("totalWeightCollectedKg", weight != null ? weight : BigDecimal.ZERO);
        stats.put("serviceArea", collector.getServiceArea());
        stats.put("vehicleNumber", collector.getVehicleNumber());
        return stats;
    }

    /**
     * Admin-level summary: platform-wide pickup counts, revenues, weight recycled.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAdminSummary() {
        Map<String, Object> stats = new HashMap<>();

        long totalPickups = pickupRepository.count();
        long completedPickups = pickupRepository.countByStatus(com.kabadiwala.entity.Pickup.Status.COMPLETED);
        BigDecimal totalRevenue = transactionRepository.sumAllFinalAmounts();
        BigDecimal totalWeight = transactionRepository.sumAllActualWeights();

        stats.put("totalPickups", totalPickups);
        stats.put("completedPickups", completedPickups);
        stats.put("pendingPickups", pickupRepository.countByStatus(com.kabadiwala.entity.Pickup.Status.REQUESTED));
        stats.put("totalRevenueINR", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        stats.put("totalWeightRecycledKg", totalWeight != null ? totalWeight : BigDecimal.ZERO);
        return stats;
    }
}
