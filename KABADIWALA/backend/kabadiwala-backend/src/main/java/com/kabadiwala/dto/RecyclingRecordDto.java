package com.kabadiwala.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RecyclingRecordDto {
    private Long id;
    private Long transactionId;
    private String transactionRef;
    private Long pickupId;
    private Long wasteCategoryId;
    private String wasteCategoryName;
    private Long wasteItemId;
    private String wasteItemName;
    private BigDecimal weight;
    private Long collectorId;
    private String collectorName;
    private Long recyclerId;
    private String recyclerName;
    private String status;
    private String handoverInfo;
    private String processingInfo;
    private BigDecimal co2SavedKg;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RecyclingRecordDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }
    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }
    public Long getPickupId() { return pickupId; }
    public void setPickupId(Long pickupId) { this.pickupId = pickupId; }
    public Long getWasteCategoryId() { return wasteCategoryId; }
    public void setWasteCategoryId(Long wasteCategoryId) { this.wasteCategoryId = wasteCategoryId; }
    public String getWasteCategoryName() { return wasteCategoryName; }
    public void setWasteCategoryName(String wasteCategoryName) { this.wasteCategoryName = wasteCategoryName; }
    public Long getWasteItemId() { return wasteItemId; }
    public void setWasteItemId(Long wasteItemId) { this.wasteItemId = wasteItemId; }
    public String getWasteItemName() { return wasteItemName; }
    public void setWasteItemName(String wasteItemName) { this.wasteItemName = wasteItemName; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public Long getCollectorId() { return collectorId; }
    public void setCollectorId(Long collectorId) { this.collectorId = collectorId; }
    public String getCollectorName() { return collectorName; }
    public void setCollectorName(String collectorName) { this.collectorName = collectorName; }
    public Long getRecyclerId() { return recyclerId; }
    public void setRecyclerId(Long recyclerId) { this.recyclerId = recyclerId; }
    public String getRecyclerName() { return recyclerName; }
    public void setRecyclerName(String recyclerName) { this.recyclerName = recyclerName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getHandoverInfo() { return handoverInfo; }
    public void setHandoverInfo(String handoverInfo) { this.handoverInfo = handoverInfo; }
    public String getProcessingInfo() { return processingInfo; }
    public void setProcessingInfo(String processingInfo) { this.processingInfo = processingInfo; }
    public BigDecimal getCo2SavedKg() { return co2SavedKg; }
    public void setCo2SavedKg(BigDecimal co2SavedKg) { this.co2SavedKg = co2SavedKg; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
