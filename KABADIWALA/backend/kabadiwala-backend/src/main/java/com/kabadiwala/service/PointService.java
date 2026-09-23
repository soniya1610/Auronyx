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
 * PointService — Module 3 stub.
 * Tracks gamification points earned/redeemed by users.
 * Full implementation in Rewards & Ecosystem Module.
 */
@Service
public class PointService {

    private final PointLedgerRepository pointLedgerRepository;

    public PointService(PointLedgerRepository pointLedgerRepository) {
        this.pointLedgerRepository = pointLedgerRepository;
    }

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

    @Transactional(readOnly = true)
    public Map<String, Integer> getMyBalance() {
        Long userId = SecurityUtils.getCurrentUserId();
        Integer earned = pointLedgerRepository.sumEarnedPointsByUserId(userId);
        Integer redeemed = pointLedgerRepository.sumRedeemedPointsByUserId(userId);
        int balance = (earned != null ? earned : 0) - (redeemed != null ? redeemed : 0);
        return Map.of(
                "earned", earned != null ? earned : 0,
                "redeemed", redeemed != null ? redeemed : 0,
                "balance", balance
        );
    }

    @Transactional(readOnly = true)
    public List<PointLedger> getMyHistory() {
        Long userId = SecurityUtils.getCurrentUserId();
        return pointLedgerRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
