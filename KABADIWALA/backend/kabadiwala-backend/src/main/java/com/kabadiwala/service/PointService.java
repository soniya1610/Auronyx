package com.kabadiwala.service;

import com.kabadiwala.entity.PointLedger;
import com.kabadiwala.entity.User;
import com.kabadiwala.repository.PointLedgerRepository;
import com.kabadiwala.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * PointService — Module 3 complete implementation.
 * Tracks gamification points earned/redeemed by users.
 */
@Service
public class PointService {

    private final PointLedgerRepository pointLedgerRepository;

    public PointService(PointLedgerRepository pointLedgerRepository) {
        this.pointLedgerRepository = pointLedgerRepository;
    }

    /**
     * Earn points for the given user (called by other services on events).
     */
    @Transactional
    public PointLedger earnPoints(User user, int points, String description, String referenceId) {
        PointLedger entry = new PointLedger();
        entry.setUser(user);
        entry.setType(PointLedger.PointType.EARNED);
        entry.setPoints(points);
        entry.setDescription(description);
        entry.setReferenceId(referenceId);
        return pointLedgerRepository.save(entry);
    }

    /**
     * Deduct (expire) points for a user.
     */
    @Transactional
    public PointLedger expirePoints(User user, int points, String description) {
        PointLedger entry = new PointLedger();
        entry.setUser(user);
        entry.setType(PointLedger.PointType.EXPIRED);
        entry.setPoints(points);
        entry.setDescription(description);
        entry.setReferenceId("EXPIRE-" + System.currentTimeMillis());
        return pointLedgerRepository.save(entry);
    }

    /**
     * Get current points balance for the authenticated user.
     */
    @Transactional(readOnly = true)
    public Map<String, Integer> getMyBalance() {
        Long userId = SecurityUtils.getCurrentUserId();
        return getBalanceForUser(userId);
    }

    /**
     * Get points balance for any user by ID (admin or internal use).
     */
    @Transactional(readOnly = true)
    public Map<String, Integer> getBalanceForUser(Long userId) {
        Integer earned   = pointLedgerRepository.sumEarnedPointsByUserId(userId);
        Integer redeemed = pointLedgerRepository.sumRedeemedPointsByUserId(userId);
        int balance = (earned != null ? earned : 0) - (redeemed != null ? redeemed : 0);
        return Map.of(
                "earned",   earned   != null ? earned   : 0,
                "redeemed", redeemed != null ? redeemed : 0,
                "balance",  balance
        );
    }

    /**
     * Get full ledger history for the authenticated user (newest first).
     */
    @Transactional(readOnly = true)
    public List<PointLedger> getMyHistory() {
        Long userId = SecurityUtils.getCurrentUserId();
        return pointLedgerRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Get full ledger history for any user (admin use).
     */
    @Transactional(readOnly = true)
    public List<PointLedger> getHistoryForUser(Long userId) {
        return pointLedgerRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
