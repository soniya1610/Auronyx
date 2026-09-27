package com.kabadiwala.service;

import com.kabadiwala.entity.*;
import com.kabadiwala.exception.InvalidTransactionException;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.PointLedgerRepository;
import com.kabadiwala.repository.UserRepository;
import com.kabadiwala.repository.ReferralRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * ReferralService — Module 3.
 * Generates and validates referral codes; awards bonus points on successful referrals.
 */
@Service
public class ReferralService {

    private static final int REFERRER_BONUS_POINTS  = 50;
    private static final int REFERRED_BONUS_POINTS  = 20;

    private final ReferralRepository referralRepository;
    private final UserRepository userRepository;
    private final PointLedgerRepository pointLedgerRepository;

    public ReferralService(ReferralRepository referralRepository,
                           UserRepository userRepository,
                           PointLedgerRepository pointLedgerRepository) {
        this.referralRepository  = referralRepository;
        this.userRepository      = userRepository;
        this.pointLedgerRepository = pointLedgerRepository;
    }

    /**
     * Generate and persist a unique referral code for the user (idempotent — returns existing if set).
     */
    @Transactional
    public String generateReferralCode(User user) {
        if (user.getReferralCode() != null && !user.getReferralCode().isBlank()) {
            return user.getReferralCode();
        }
        String code;
        do {
            code = "KBD-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        } while (userRepository.existsByReferralCode(code));

        user.setReferralCode(code);
        userRepository.save(user);
        return code;
    }

    /**
     * Apply referral code during new user registration.
     * Awards bonus points to both the referrer and the new user.
     * Safe to call after the new user has been persisted.
     */
    @Transactional
    public Optional<Referral> applyReferralCode(User newUser, String referralCode) {
        if (referralCode == null || referralCode.isBlank()) return Optional.empty();

        // Prevent a user from referring themselves
        if (referralCode.equals(newUser.getReferralCode())) return Optional.empty();

        // Prevent double application
        if (referralRepository.existsByReferredId(newUser.getId())) return Optional.empty();

        User referrer = userRepository.findByReferralCode(referralCode).orElse(null);
        if (referrer == null) return Optional.empty(); // silently ignore invalid code

        Referral referral = new Referral();
        referral.setReferrer(referrer);
        referral.setReferred(newUser);
        referral.setReferralCode(referralCode);
        referral.setBonusPointsAwarded(REFERRER_BONUS_POINTS + REFERRED_BONUS_POINTS);
        referral.setBonusApplied(true);
        Referral saved = referralRepository.save(referral);

        // Award points to referrer
        awardPoints(referrer, REFERRER_BONUS_POINTS,
                "Referral bonus: " + newUser.getName() + " joined using your code",
                "REFERRAL-" + saved.getId());

        // Award points to new user
        awardPoints(newUser, REFERRED_BONUS_POINTS,
                "Welcome bonus for joining via referral",
                "REFERRAL-WELCOME-" + saved.getId());

        return Optional.of(saved);
    }

    @Transactional(readOnly = true)
    public List<Referral> getMyReferrals(Long userId) {
        return referralRepository.findByReferrerId(userId);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getReferralStats(Long userId) {
        long totalReferrals = referralRepository.countByReferrerId(userId);
        return Map.of(
                "totalReferrals", totalReferrals,
                "referrerBonusPerReferral", REFERRER_BONUS_POINTS
        );
    }

    private void awardPoints(User user, int points, String description, String referenceId) {
        PointLedger entry = new PointLedger();
        entry.setUser(user);
        entry.setType(PointLedger.PointType.EARNED);
        entry.setPoints(points);
        entry.setDescription(description);
        entry.setReferenceId(referenceId);
        pointLedgerRepository.save(entry);
    }
}
