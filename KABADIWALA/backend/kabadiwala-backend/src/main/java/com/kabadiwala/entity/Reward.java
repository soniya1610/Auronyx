package com.kabadiwala.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Module 3 placeholder — stub entity for reward catalog.
 * Full implementation deferred to Rewards & Ecosystem Module.
 */
@Entity
@Table(name = "rewards")
public class Reward {

    public enum RewardType { CASH_BACK, VOUCHER, BADGE, DONATION }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private RewardType type;

    @Column(name = "points_cost", nullable = false)
    private Integer pointsCost = 0;

    @Column(name = "cash_value", precision = 10, scale = 2)
    private BigDecimal cashValue;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "stock_limit")
    private Integer stockLimit;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public RewardType getType() { return type; }
    public void setType(RewardType type) { this.type = type; }
    public Integer getPointsCost() { return pointsCost; }
    public void setPointsCost(Integer pointsCost) { this.pointsCost = pointsCost; }
    public BigDecimal getCashValue() { return cashValue; }
    public void setCashValue(BigDecimal cashValue) { this.cashValue = cashValue; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public Integer getStockLimit() { return stockLimit; }
    public void setStockLimit(Integer stockLimit) { this.stockLimit = stockLimit; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
