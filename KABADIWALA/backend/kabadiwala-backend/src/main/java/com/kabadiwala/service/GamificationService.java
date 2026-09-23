package com.kabadiwala.service;

import com.kabadiwala.entity.PointLedger;
import com.kabadiwala.entity.User;
import com.kabadiwala.repository.PointLedgerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * GamificationService — Module 3 stub.
 * Awards points for completed pickups and milestones.
 * Full implementation deferred to Rewards & Ecosystem Module.
 */
@Service
public class GamificationService {

    private static final int POINTS_PER_PICKUP = 10;
    private final PointLedgerRepository pointLedgerRepository;

    public GamificationService(PointLedgerRepository pointLedgerRepository) {
        this.pointLedgerRepository = pointLedgerRepository;
    }

    /**
     * Award points to user upon successful pickup completion.
     */
    @Transactional
    public void awardPickupCompletionPoints(User user, Long pickupId) {
        PointLedger entry = new PointLedger();
        entry.setUser(user);
        entry.setType(PointLedger.PointType.EARNED);
        entry.setPoints(POINTS_PER_PICKUP);
        entry.setDescription("Points for completing pickup #" + pickupId);
        entry.setReferenceId("PICKUP-" + pickupId);
        pointLedgerRepository.save(entry);
    }
}
