package com.kabadiwala.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VerificationDto {
    private Long id;
    private Long pickupId;
    private Long collectorId;
    private String collectorName;
    private Long actualCategoryId;
    private String actualCategoryName;
    private Long actualWasteItemId;
    private String actualWasteItemName;
    private BigDecimal actualWeight;
    private BigDecimal finalAmount;
    private String condition;
    private String notes;
    private LocalDateTime verifiedAt;

    public VerificationDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPickupId() { return pickupId; }
    public void setPickupId(Long pickupId) { this.pickupId = pickupId; }
    public Long getCollectorId() { return collectorId; }
    public void setCollectorId(Long collectorId) { this.collectorId = collectorId; }
    public String getCollectorName() { return collectorName; }
    public void setCollectorName(String collectorName) { this.collectorName = collectorName; }
    public Long getActualCategoryId() { return actualCategoryId; }
    public void setActualCategoryId(Long actualCategoryId) { this.actualCategoryId = actualCategoryId; }
    public String getActualCategoryName() { return actualCategoryName; }
    public void setActualCategoryName(String actualCategoryName) { this.actualCategoryName = actualCategoryName; }
    public Long getActualWasteItemId() { return actualWasteItemId; }
    public void setActualWasteItemId(Long actualWasteItemId) { this.actualWasteItemId = actualWasteItemId; }
    public String getActualWasteItemName() { return actualWasteItemName; }
    public void setActualWasteItemName(String actualWasteItemName) { this.actualWasteItemName = actualWasteItemName; }
    public BigDecimal getActualWeight() { return actualWeight; }
    public void setActualWeight(BigDecimal actualWeight) { this.actualWeight = actualWeight; }
    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
}
