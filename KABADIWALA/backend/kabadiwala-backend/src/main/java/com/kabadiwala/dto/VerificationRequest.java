package com.kabadiwala.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class VerificationRequest {

    @NotNull(message = "Pickup ID is required")
    private Long pickupId;

    @NotNull(message = "Actual category ID is required")
    private Long actualCategoryId;

    private Long actualWasteItemId;

    @NotNull(message = "Actual weight is required")
    @DecimalMin(value = "0.001", message = "Weight must be greater than 0")
    private BigDecimal actualWeight;

    private String condition;

    private String notes;

    public VerificationRequest() {}

    public Long getPickupId() { return pickupId; }
    public void setPickupId(Long pickupId) { this.pickupId = pickupId; }
    public Long getActualCategoryId() { return actualCategoryId; }
    public void setActualCategoryId(Long actualCategoryId) { this.actualCategoryId = actualCategoryId; }
    public Long getActualWasteItemId() { return actualWasteItemId; }
    public void setActualWasteItemId(Long actualWasteItemId) { this.actualWasteItemId = actualWasteItemId; }
    public BigDecimal getActualWeight() { return actualWeight; }
    public void setActualWeight(BigDecimal actualWeight) { this.actualWeight = actualWeight; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
