package com.kabadiwala.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Module 3 — Referral entity.
 * Tracks user referrals: who referred whom, and whether the bonus was applied.
 */
@Entity
@Table(name = "referrals")
public class Referral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user who shared the referral code. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referrer_id", nullable = false)
    private User referrer;

    /** The newly registered user who used the code. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referred_id", nullable = false, unique = true)
    private User referred;

    @Column(name = "referral_code", nullable = false, length = 20)
    private String referralCode;

    @Column(name = "bonus_points_awarded", nullable = false)
    private Integer bonusPointsAwarded = 0;

    @Column(name = "bonus_applied", nullable = false)
    private Boolean bonusApplied = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getReferrer() { return referrer; }
    public void setReferrer(User referrer) { this.referrer = referrer; }
    public User getReferred() { return referred; }
    public void setReferred(User referred) { this.referred = referred; }
    public String getReferralCode() { return referralCode; }
    public void setReferralCode(String referralCode) { this.referralCode = referralCode; }
    public Integer getBonusPointsAwarded() { return bonusPointsAwarded; }
    public void setBonusPointsAwarded(Integer bonusPointsAwarded) { this.bonusPointsAwarded = bonusPointsAwarded; }
    public Boolean getBonusApplied() { return bonusApplied; }
    public void setBonusApplied(Boolean bonusApplied) { this.bonusApplied = bonusApplied; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
