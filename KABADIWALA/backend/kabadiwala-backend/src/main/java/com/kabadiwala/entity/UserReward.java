package com.kabadiwala.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "user_rewards")
public class UserReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private Long rewardId;

    private String rewardTitle;

    private String couponCode;

    private Integer pointsSpent;

    private Instant redeemedAt;

    public UserReward() {
    }

    public UserReward(Long userId, Long rewardId, String rewardTitle, String couponCode, Integer pointsSpent) {
        this.userId = userId;
        this.rewardId = rewardId;
        this.rewardTitle = rewardTitle;
        this.couponCode = couponCode;
        this.pointsSpent = pointsSpent;
        this.redeemedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRewardId() {
        return rewardId;
    }

    public void setRewardId(Long rewardId) {
        this.rewardId = rewardId;
    }

    public String getRewardTitle() {
        return rewardTitle;
    }

    public void setRewardTitle(String rewardTitle) {
        this.rewardTitle = rewardTitle;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public Integer getPointsSpent() {
        return pointsSpent;
    }

    public void setPointsSpent(Integer pointsSpent) {
        this.pointsSpent = pointsSpent;
    }

    public Instant getRedeemedAt() {
        return redeemedAt;
    }

    public void setRedeemedAt(Instant redeemedAt) {
        this.redeemedAt = redeemedAt;
    }
}
