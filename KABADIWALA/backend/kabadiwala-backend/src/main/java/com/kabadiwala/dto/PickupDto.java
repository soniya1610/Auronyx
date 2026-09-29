package com.kabadiwala.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PickupDto {
    private Long id;
    private Long userId;
    private String userName;
    private Long wasteCategoryId;
    private String wasteCategoryName;
    private Long wasteItemId;
    private String wasteItemName;
    private Integer estimatedQuantity;
    private BigDecimal estimatedWeight;
    private String addressLine;
    private String city;
    private String state;
    private String pincode;
    private Double latitude;
    private Double longitude;
    private LocalDate scheduledDate;
    private String scheduledTime;
    private String notes;
    private Long assignedCollectorId;
    private String assignedCollectorName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Double distanceKm;

    public PickupDto() {}

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public Long getWasteCategoryId() { return wasteCategoryId; }
    public void setWasteCategoryId(Long wasteCategoryId) { this.wasteCategoryId = wasteCategoryId; }
    public String getWasteCategoryName() { return wasteCategoryName; }
    public void setWasteCategoryName(String wasteCategoryName) { this.wasteCategoryName = wasteCategoryName; }
    public Long getWasteItemId() { return wasteItemId; }
    public void setWasteItemId(Long wasteItemId) { this.wasteItemId = wasteItemId; }
    public String getWasteItemName() { return wasteItemName; }
    public void setWasteItemName(String wasteItemName) { this.wasteItemName = wasteItemName; }
    public Integer getEstimatedQuantity() { return estimatedQuantity; }
    public void setEstimatedQuantity(Integer estimatedQuantity) { this.estimatedQuantity = estimatedQuantity; }
    public BigDecimal getEstimatedWeight() { return estimatedWeight; }
    public void setEstimatedWeight(BigDecimal estimatedWeight) { this.estimatedWeight = estimatedWeight; }
    public String getAddressLine() { return addressLine; }
    public void setAddressLine(String addressLine) { this.addressLine = addressLine; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public LocalDate getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }
    public String getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Long getAssignedCollectorId() { return assignedCollectorId; }
    public void setAssignedCollectorId(Long assignedCollectorId) { this.assignedCollectorId = assignedCollectorId; }
    public String getAssignedCollectorName() { return assignedCollectorName; }
    public void setAssignedCollectorName(String assignedCollectorName) { this.assignedCollectorName = assignedCollectorName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Frontend compatibility helpers
    public String getWasteType() {
        return wasteCategoryName != null ? wasteCategoryName : "Mixed Recyclables";
    }

    public Double getEstimatedWeightKg() {
        return estimatedWeight != null ? estimatedWeight.doubleValue() : 0.0;
    }

    public String getAddress() {
        return addressLine;
    }

    public Double getTotalAmount() {
        return 0.0;
    }

    public String getVerificationCode() {
        return id != null ? String.format("%04d", Math.abs((id * 31 + 1000) % 9000)) : "4819";
    }
}
