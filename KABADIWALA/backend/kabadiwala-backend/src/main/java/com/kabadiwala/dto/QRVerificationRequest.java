package com.kabadiwala.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class QRVerificationRequest {

    @NotNull(message = "Pickup ID is required")
    private Long pickupId;

    @NotBlank(message = "QR code is required")
    private String qrCode;

    private String scannedLocation;
    private Double latitude;
    private Double longitude;

    // Getters and Setters
    public Long getPickupId() { return pickupId; }
    public void setPickupId(Long pickupId) { this.pickupId = pickupId; }
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    public String getScannedLocation() { return scannedLocation; }
    public void setScannedLocation(String scannedLocation) { this.scannedLocation = scannedLocation; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
