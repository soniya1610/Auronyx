package com.kabadiwala.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class FinalPriceRequest {

    @NotNull(message = "Pickup ID is required")
    private Long pickupId;

    @NotNull(message = "Waste category ID is required")
    private Long wasteCategoryId;

    @NotNull(message = "Actual weight is required")
    @Positive(message = "Weight must be positive")
    private BigDecimal actualWeight;

    private Long wasteItemId;

    // Getters and Setters
    public Long getPickupId() { return pickupId; }
    public void setPickupId(Long pickupId) { this.pickupId = pickupId; }
    public Long getWasteCategoryId() { return wasteCategoryId; }
    public void setWasteCategoryId(Long wasteCategoryId) { this.wasteCategoryId = wasteCategoryId; }
    public BigDecimal getActualWeight() { return actualWeight; }
    public void setActualWeight(BigDecimal actualWeight) { this.actualWeight = actualWeight; }
    public Long getWasteItemId() { return wasteItemId; }
    public void setWasteItemId(Long wasteItemId) { this.wasteItemId = wasteItemId; }
}
