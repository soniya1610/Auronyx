package com.kabadiwala.service;

import com.kabadiwala.entity.*;
import com.kabadiwala.repository.FraudAlertRepository;
import com.kabadiwala.repository.PickupRepository;
import com.kabadiwala.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Fraud Detection Service — Module 2.
 * Analyzes pickup data for anomalies: extreme weight claims, rapid sequential pickups,
 * or impossible pricing values.
 */
@Service
public class FraudDetectionService {

    private final FraudAlertRepository fraudAlertRepository;
    private final PickupRepository pickupRepository;

    public FraudDetectionService(FraudAlertRepository fraudAlertRepository,
                                  PickupRepository pickupRepository) {
        this.fraudAlertRepository = fraudAlertRepository;
        this.pickupRepository = pickupRepository;
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

    @Transactional(readOnly = true)
    public List<FraudAlert> getOpenAlerts() {
        return fraudAlertRepository.findByStatus(FraudAlert.AlertStatus.OPEN);
    }

    @Transactional(readOnly = true)
    public List<FraudAlert> getAllAlerts() {
        return fraudAlertRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public FraudAlert resolveAlert(Long alertId, String reviewNotes) {
        FraudAlert alert = fraudAlertRepository.findById(alertId)
                .orElseThrow(() -> new com.kabadiwala.exception.ResourceNotFoundException("FraudAlert", "id", alertId));
        alert.setStatus(FraudAlert.AlertStatus.RESOLVED);
        alert.setReviewedBy(SecurityUtils.getCurrentUser().getEmail());
        alert.setReviewNotes(reviewNotes);
        alert.setReviewedAt(java.time.LocalDateTime.now());
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
