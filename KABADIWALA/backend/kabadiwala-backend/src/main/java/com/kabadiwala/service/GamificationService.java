package com.kabadiwala.service;

import com.kabadiwala.entity.*;
import com.kabadiwala.repository.BadgeRepository;
import com.kabadiwala.repository.ChallengeRepository;
import com.kabadiwala.repository.PickupRepository;
import com.kabadiwala.repository.PointLedgerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * GamificationService — Module 3 complete implementation.
 * Awards points for events, unlocks badges, tracks challenge progress.
 */
@Service
public class GamificationService {

    private static final Logger log = LoggerFactory.getLogger(GamificationService.class);

    // Point constants
    public static final int POINTS_PER_PICKUP          = 10;
    public static final int POINTS_PER_KG_RECYCLED     = 5;
    public static final int MILESTONE_10_PICKUPS_BONUS  = 100;
    public static final int MILESTONE_50_PICKUPS_BONUS  = 500;

    private final PointLedgerRepository pointLedgerRepository;
    private final BadgeRepository badgeRepository;
    private final ChallengeRepository challengeRepository;
    private final PickupRepository pickupRepository;

    public GamificationService(PointLedgerRepository pointLedgerRepository,
                                BadgeRepository badgeRepository,
                                ChallengeRepository challengeRepository,
                                PickupRepository pickupRepository) {
        this.pointLedgerRepository = pointLedgerRepository;
        this.badgeRepository       = badgeRepository;
        this.challengeRepository   = challengeRepository;
        this.pickupRepository      = pickupRepository;
    }

    /**
     * Award points upon successful pickup completion.
     * Also checks for milestone bonuses.
     */
    @Transactional
    public void awardPickupCompletionPoints(User user, Long pickupId) {
        // Base pickup points
        savePoints(user, POINTS_PER_PICKUP,
                "Points for completing pickup #" + pickupId,
                "PICKUP-" + pickupId);

        // Milestone check
        long completedPickups = pickupRepository.countByUserIdAndStatus(
                user.getId(), Pickup.Status.COMPLETED);

        if (completedPickups == 10) {
            savePoints(user, MILESTONE_10_PICKUPS_BONUS,
                    "Milestone bonus: 10 pickups completed!", "MILESTONE-10-" + user.getId());
            log.info("User {} reached 10-pickup milestone", user.getId());
        } else if (completedPickups == 50) {
            savePoints(user, MILESTONE_50_PICKUPS_BONUS,
                    "Milestone bonus: 50 pickups completed!", "MILESTONE-50-" + user.getId());
            log.info("User {} reached 50-pickup milestone", user.getId());
        }
    }

    /**
     * Award weight-based points when recycling record is completed.
     * e.g. 5 points per kg recycled.
     */
    @Transactional
    public void awardRecyclingPoints(User user, double weightKg, Long recyclingRecordId) {
        int points = (int) Math.round(weightKg * POINTS_PER_KG_RECYCLED);
        if (points <= 0) return;
        savePoints(user, points,
                String.format("Recycling reward: %.2f kg recycled", weightKg),
                "RECYCLING-" + recyclingRecordId);
    }

    /**
     * Get all active challenges visible to users.
     */
    @Transactional(readOnly = true)
    public List<Challenge> getActiveChallenges() {
        return challengeRepository.findByStatus(Challenge.ChallengeStatus.ACTIVE);
    }

    /**
     * Get all badges unlocked by the given user based on their total earned points.
     */
    @Transactional(readOnly = true)
    public List<Badge> getEarnedBadges(Long userId) {
        Integer totalEarned = pointLedgerRepository.sumEarnedPointsByUserId(userId);
        int points = totalEarned != null ? totalEarned : 0;
        return badgeRepository.findByPointsRequiredLessThanEqualOrderByPointsRequiredDesc(points);
    }

    /**
     * Get a user's gamification summary: points balance, badge count, completed pickups.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getUserGamificationSummary(Long userId) {
        Integer earned   = pointLedgerRepository.sumEarnedPointsByUserId(userId);
        Integer redeemed = pointLedgerRepository.sumRedeemedPointsByUserId(userId);
        int balance = (earned != null ? earned : 0) - (redeemed != null ? redeemed : 0);
        List<Badge> badges = getEarnedBadges(userId);
        long completedPickups = pickupRepository.countByUserIdAndStatus(userId, Pickup.Status.COMPLETED);

        return Map.of(
                "totalPointsEarned", earned != null ? earned : 0,
                "totalPointsRedeemed", redeemed != null ? redeemed : 0,
                "currentBalance", balance,
                "badgesUnlocked", badges.size(),
                "badges", badges,
                "completedPickups", completedPickups
        );
    }

    // ---- Admin operations ----

    /**
     * Admin: create or update a challenge.
     */
    @Transactional
    public Challenge saveChallenge(Challenge challenge) {
        return challengeRepository.save(challenge);
    }

    /**
     * Admin: manually award bonus points to any user (e.g., customer support).
     */
    @Transactional
    public PointLedger adminAwardPoints(User user, int points, String reason) {
        return savePoints(user, points, "[Admin] " + reason, "ADMIN-" + System.currentTimeMillis());
    }

    // ---- Badge Management ----

    @Transactional(readOnly = true)
    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    @Transactional
    public Badge saveBadge(Badge badge) {
        return badgeRepository.save(badge);
    }

    // ---- Private helpers ----

    private PointLedger savePoints(User user, int points, String description, String referenceId) {
        PointLedger entry = new PointLedger();
        entry.setUser(user);
        entry.setType(PointLedger.PointType.EARNED);
        entry.setPoints(points);
        entry.setDescription(description);
        entry.setReferenceId(referenceId);
        return pointLedgerRepository.save(entry);
    }
}
