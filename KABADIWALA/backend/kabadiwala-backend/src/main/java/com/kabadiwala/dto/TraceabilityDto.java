package com.kabadiwala.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TraceabilityDto {
    private String qrCode;
    private String transactionRef;
    private Long pickupId;
    private String category;
    private String item;
    private BigDecimal weight;
    private String city;
    private String pickupStatus;
    private String recyclingStatus;
    private BigDecimal co2SavedKg;
    private String collectorName;
    private String recyclerName;
    private String processingInfo;
    private LocalDateTime verifiedAt;
    private String qrBase64;
    private String trackingUrl;

    public TraceabilityDto() {}

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }
    public Long getPickupId() { return pickupId; }
    public void setPickupId(Long pickupId) { this.pickupId = pickupId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getPickupStatus() { return pickupStatus; }
    public void setPickupStatus(String pickupStatus) { this.pickupStatus = pickupStatus; }
    public String getRecyclingStatus() { return recyclingStatus; }
    public void setRecyclingStatus(String recyclingStatus) { this.recyclingStatus = recyclingStatus; }
    public BigDecimal getCo2SavedKg() { return co2SavedKg; }
    public void setCo2SavedKg(BigDecimal co2SavedKg) { this.co2SavedKg = co2SavedKg; }
    public String getCollectorName() { return collectorName; }
    public void setCollectorName(String collectorName) { this.collectorName = collectorName; }
    public String getRecyclerName() { return recyclerName; }
    public void setRecyclerName(String recyclerName) { this.recyclerName = recyclerName; }
    public String getProcessingInfo() { return processingInfo; }
    public void setProcessingInfo(String processingInfo) { this.processingInfo = processingInfo; }
    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
    public String getQrBase64() { return qrBase64; }
    public void setQrBase64(String qrBase64) { this.qrBase64 = qrBase64; }
    public String getTrackingUrl() { return trackingUrl; }
    public void setTrackingUrl(String trackingUrl) { this.trackingUrl = trackingUrl; }
}
