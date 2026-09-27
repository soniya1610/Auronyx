package com.kabadiwala.service;

import com.kabadiwala.entity.*;
import com.kabadiwala.exception.InvalidTransactionException;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.PointLedgerRepository;
import com.kabadiwala.repository.RedemptionRepository;
import com.kabadiwala.repository.RewardRepository;
import com.kabadiwala.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * RedemptionService — Module 3 complete implementation.
 * Handles reward redemption: balance check, point deduction, wallet credit.
 */
@Service
public class RedemptionService {

    private static final int POINTS_PER_RUPEE = 10; // 100 points = ₹10

    private final RedemptionRepository redemptionRepository;
    private final RewardRepository rewardRepository;
    private final PointLedgerRepository pointLedgerRepository;
    private final WalletService walletService;

    public RedemptionService(RedemptionRepository redemptionRepository,
                              RewardRepository rewardRepository,
                              PointLedgerRepository pointLedgerRepository,
                              WalletService walletService) {
        this.redemptionRepository = redemptionRepository;
        this.rewardRepository = rewardRepository;
        this.pointLedgerRepository = pointLedgerRepository;
        this.walletService = walletService;
    }

    @Transactional(readOnly = true)
    public List<Redemption> getMyRedemptions() {
        Long userId = SecurityUtils.getCurrentUserId();
        return redemptionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Reward> getAvailableRewards() {
        return rewardRepository.findByActiveTrue();
    }

    /**
     * Redeem a reward using points.
     * Validates: reward exists & active, sufficient balance, stock available.
     * On success: deducts points, credits wallet (for CASH_BACK type), saves redemption.
     */
    @Transactional
    public Redemption redeemReward(Long rewardId) {
        User currentUser = SecurityUtils.getCurrentUser();
        Long userId = currentUser.getId();

        // Load and validate reward
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new ResourceNotFoundException("Reward", "id", rewardId));

        if (!reward.getActive()) {
            throw new InvalidTransactionException("Reward '" + reward.getTitle() + "' is no longer active.");
        }

        // Check stock limit
        if (reward.getStockLimit() != null && reward.getStockLimit() > 0) {
            long usedCount = redemptionRepository.findByStatus(Redemption.RedemptionStatus.COMPLETED)
                    .stream().filter(r -> rewardId.equals(r.getReward() != null ? r.getReward().getId() : null)).count();
            if (usedCount >= reward.getStockLimit()) {
                throw new InvalidTransactionException("Reward '" + reward.getTitle() + "' is out of stock.");
            }
        }

        // Check point balance
        Integer earned   = pointLedgerRepository.sumEarnedPointsByUserId(userId);
        Integer redeemed = pointLedgerRepository.sumRedeemedPointsByUserId(userId);
        int balance = (earned != null ? earned : 0) - (redeemed != null ? redeemed : 0);

        if (balance < reward.getPointsCost()) {
            throw new InvalidTransactionException(
                    "Insufficient points. You have " + balance + " but need " + reward.getPointsCost() + ".");
        }

        // Deduct points from ledger
        PointLedger deduction = new PointLedger();
        deduction.setUser(currentUser);
        deduction.setType(PointLedger.PointType.REDEEMED);
        deduction.setPoints(reward.getPointsCost());
        deduction.setDescription("Redeemed: " + reward.getTitle());
        deduction.setReferenceId("REWARD-" + rewardId);
        pointLedgerRepository.save(deduction);

        // Build redemption record
        Redemption redemption = new Redemption();
        redemption.setUser(currentUser);
        redemption.setReward(reward);
        redemption.setPointsUsed(reward.getPointsCost());
        redemption.setCashValue(reward.getCashValue());
        redemption.setReferenceNo("RDM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        redemption.setStatus(Redemption.RedemptionStatus.COMPLETED);
        Redemption saved = redemptionRepository.save(redemption);

        // Credit wallet for CASH_BACK type rewards
        if (reward.getType() == Reward.RewardType.CASH_BACK && reward.getCashValue() != null) {
            Wallet wallet = walletService.getOrCreateWallet(currentUser);
            walletService.credit(wallet, reward.getCashValue(), "REDEMPTION", saved.getId().toString());
        }

        return saved;
    }

    /**
     * Admin: get all pending redemptions for manual review/fulfilment.
     */
    @Transactional(readOnly = true)
    public List<Redemption> getPendingRedemptions() {
        return redemptionRepository.findByStatus(Redemption.RedemptionStatus.PENDING);
    }

    /**
     * Admin: approve a pending redemption.
     */
    @Transactional
    public Redemption approveRedemption(Long redemptionId) {
        Redemption r = redemptionRepository.findById(redemptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Redemption", "id", redemptionId));
        if (r.getStatus() != Redemption.RedemptionStatus.PENDING) {
            throw new InvalidTransactionException("Redemption is already " + r.getStatus());
        }
        r.setStatus(Redemption.RedemptionStatus.COMPLETED);
        return redemptionRepository.save(r);
    }

    /**
     * Admin: reject a pending redemption (refund points).
     */
    @Transactional
    public Redemption rejectRedemption(Long redemptionId) {
        Redemption r = redemptionRepository.findById(redemptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Redemption", "id", redemptionId));
        if (r.getStatus() != Redemption.RedemptionStatus.PENDING) {
            throw new InvalidTransactionException("Redemption is already " + r.getStatus());
        }
        r.setStatus(Redemption.RedemptionStatus.REJECTED);
        redemptionRepository.save(r);

        // Refund points
        PointLedger refund = new PointLedger();
        refund.setUser(r.getUser());
        refund.setType(PointLedger.PointType.EARNED);
        refund.setPoints(r.getPointsUsed());
        refund.setDescription("Points refunded: redemption #" + redemptionId + " rejected");
        refund.setReferenceId("REFUND-" + redemptionId);
        pointLedgerRepository.save(refund);

        return r;
    }
}
