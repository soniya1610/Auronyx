package com.kabadiwala.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class RedemptionRequest {

    @NotNull(message = "Reward ID is required")
    private Long rewardId;

    @NotNull(message = "Points to redeem is required")
    private Integer pointsToRedeem;

    private String redemptionMethod; // WALLET, UPI, VOUCHER

    private String upiId;

    // Getters and Setters
    public Long getRewardId() { return rewardId; }
    public void setRewardId(Long rewardId) { this.rewardId = rewardId; }
    public Integer getPointsToRedeem() { return pointsToRedeem; }
    public void setPointsToRedeem(Integer pointsToRedeem) { this.pointsToRedeem = pointsToRedeem; }
    public String getRedemptionMethod() { return redemptionMethod; }
    public void setRedemptionMethod(String redemptionMethod) { this.redemptionMethod = redemptionMethod; }
    public String getUpiId() { return upiId; }
    public void setUpiId(String upiId) { this.upiId = upiId; }
}
