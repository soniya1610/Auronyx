package com.kabadiwala.service;

import com.kabadiwala.entity.*;
import com.kabadiwala.repository.FraudAlertRepository;
import com.kabadiwala.repository.PickupRepository;
import com.kabadiwala.repository.RedemptionRepository;
import com.kabadiwala.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Fraud Detection Service — Modules 2 & 3.
 * Analyzes pickup data and reward redemptions for anomalies.
 */
@Service
public class FraudDetectionService {

    private final FraudAlertRepository fraudAlertRepository;
    private final PickupRepository pickupRepository;
    private final RedemptionRepository redemptionRepository;

    public FraudDetectionService(FraudAlertRepository fraudAlertRepository,
                                  PickupRepository pickupRepository,
                                  RedemptionRepository redemptionRepository) {
        this.fraudAlertRepository = fraudAlertRepository;
        this.pickupRepository     = pickupRepository;
        this.redemptionRepository = redemptionRepository;
    }

    /**
     * Perform fraud checks after a pickup is verified.
     * Any anomaly creates a FraudAlert for admin review.
     */
    @Transactional
    public void analyzePickup(Pickup pickup, BigDecimal actualWeight) {
        // Rule 1: Extreme weight claim (> 500 kg single pickup)
        if (actualWeight != null && actualWeight.compareTo(new BigDecimal("500")) > 0) {
            createAlert(pickup, pickup.getUser(),
                    "EXTREME_WEIGHT",
                    "Suspicious pickup weight: " + actualWeight + " kg exceeds 500 kg threshold.",
                    FraudAlert.Severity.HIGH, 0.90);
        }

        // Rule 2: Zero or negative weight (data integrity)
        if (actualWeight != null && actualWeight.compareTo(BigDecimal.ZERO) <= 0) {
            createAlert(pickup, pickup.getUser(),
                    "ZERO_WEIGHT",
                    "Pickup recorded with zero or negative weight: " + actualWeight,
                    FraudAlert.Severity.HIGH, 0.99);
        }

        // Rule 3: More than 5 pickups by same user in same day
        if (pickup.getUser() != null && pickup.getScheduledDate() != null) {
            long sameDay = pickupRepository.countByUserIdAndScheduledDate(
                    pickup.getUser().getId(), pickup.getScheduledDate());
            if (sameDay > 5) {
                createAlert(pickup, pickup.getUser(),
                        "RAPID_PICKUPS",
                        "User has " + sameDay + " pickups on the same day.",
                        FraudAlert.Severity.MEDIUM, 0.75);
            }
        }
    }

    /**
     * Module 3: Analyze reward redemption for suspicious patterns.
     * Flags users who attempt rapid consecutive redemptions.
     */
    @Transactional
    public void analyzeRedemption(User user, Long rewardId) {
        if (user == null) return;

        // Rule: more than 3 redemptions within 1 hour
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<Redemption> recent = redemptionRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        long recentCount = recent.stream()
                .filter(r -> r.getCreatedAt() != null && r.getCreatedAt().isAfter(oneHourAgo))
                .count();

        if (recentCount > 3) {
            createAlert(null, user,
                    "RAPID_REDEMPTIONS",
                    "User redeemed rewards " + recentCount + " times in the last hour (reward id: " + rewardId + ").",
                    FraudAlert.Severity.MEDIUM, 0.70);
        }
    }

    @Transactional(readOnly = true)
    public List<FraudAlert> getOpenAlerts() {
        return fraudAlertRepository.findByStatus(FraudAlert.AlertStatus.OPEN);
    }

    @Transactional(readOnly = true)
    public List<FraudAlert> getAllAlerts() {
        return fraudAlertRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<FraudAlert> getAlertsByUser(Long userId) {
        return fraudAlertRepository.findByUserId(userId);
    }

    @Transactional
    public FraudAlert resolveAlert(Long alertId, String reviewNotes) {
        FraudAlert alert = fraudAlertRepository.findById(alertId)
                .orElseThrow(() -> new com.kabadiwala.exception.ResourceNotFoundException("FraudAlert", "id", alertId));
        alert.setStatus(FraudAlert.AlertStatus.RESOLVED);
        alert.setReviewedBy(SecurityUtils.getCurrentUser().getEmail());
        alert.setReviewNotes(reviewNotes);
        alert.setReviewedAt(LocalDateTime.now());
        return fraudAlertRepository.save(alert);
    }

    @Transactional
    public FraudAlert markFalsePositive(Long alertId, String reviewNotes) {
        FraudAlert alert = fraudAlertRepository.findById(alertId)
                .orElseThrow(() -> new com.kabadiwala.exception.ResourceNotFoundException("FraudAlert", "id", alertId));
        alert.setStatus(FraudAlert.AlertStatus.FALSE_POSITIVE);
        alert.setReviewedBy(SecurityUtils.getCurrentUser().getEmail());
        alert.setReviewNotes(reviewNotes);
        alert.setReviewedAt(LocalDateTime.now());
        return fraudAlertRepository.save(alert);
    }

    private void createAlert(Pickup pickup, User user, String type, String description,
                              FraudAlert.Severity severity, double confidence) {
        FraudAlert alert = new FraudAlert();
        alert.setPickup(pickup);
        alert.setUser(user);
        alert.setAlertType(type);
        alert.setDescription(description);
        alert.setSeverity(severity);
        alert.setStatus(FraudAlert.AlertStatus.OPEN);
        alert.setConfidenceScore(confidence);
        fraudAlertRepository.save(alert);
    }
}
