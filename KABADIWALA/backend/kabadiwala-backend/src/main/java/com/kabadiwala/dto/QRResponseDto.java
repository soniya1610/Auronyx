package com.kabadiwala.dto;

public class QRResponseDto {
    private String qrCode;
    private String qrBase64;
    private String trackingUrl;
    private TraceabilityDto details;

    public QRResponseDto() {}

    public QRResponseDto(String qrCode, String qrBase64, String trackingUrl, TraceabilityDto details) {
        this.qrCode = qrCode;
        this.qrBase64 = qrBase64;
        this.trackingUrl = trackingUrl;
        this.details = details;
    }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    public String getQrBase64() { return qrBase64; }
    public void setQrBase64(String qrBase64) { this.qrBase64 = qrBase64; }
    public String getTrackingUrl() { return trackingUrl; }
    public void setTrackingUrl(String trackingUrl) { this.trackingUrl = trackingUrl; }
    public TraceabilityDto getDetails() { return details; }
    public void setDetails(TraceabilityDto details) { this.details = details; }
}
