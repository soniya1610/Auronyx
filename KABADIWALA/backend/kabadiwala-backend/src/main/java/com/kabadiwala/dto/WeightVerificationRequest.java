package com.kabadiwala.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class WeightVerificationRequest {

    @NotNull(message = "Pickup ID is required")
    private Long pickupId;

    @NotNull(message = "Actual weight is required")
    @DecimalMin(value = "0.001", message = "Weight must be at least 0.001 kg")
    private BigDecimal actualWeight;

    private Long actualCategoryId;
    private String condition;
    private String notes;

    // Getters and Setters
    public Long getPickupId() { return pickupId; }
    public void setPickupId(Long pickupId) { this.pickupId = pickupId; }
    public BigDecimal getActualWeight() { return actualWeight; }
    public void setActualWeight(BigDecimal actualWeight) { this.actualWeight = actualWeight; }
    public Long getActualCategoryId() { return actualCategoryId; }
    public void setActualCategoryId(Long actualCategoryId) { this.actualCategoryId = actualCategoryId; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
