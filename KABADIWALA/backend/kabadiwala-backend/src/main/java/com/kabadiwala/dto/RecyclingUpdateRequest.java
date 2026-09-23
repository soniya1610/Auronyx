package com.kabadiwala.dto;

import jakarta.validation.constraints.NotBlank;

public class RecyclingUpdateRequest {

    @NotBlank(message = "Status is required (e.g. SORTED, AGGREGATED, TRANSPORT, RECEIVED, PROCESSING, RECYCLED)")
    private String status;

    private String handoverInfo;
    private String processingInfo;

    public RecyclingUpdateRequest() {}

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getHandoverInfo() { return handoverInfo; }
    public void setHandoverInfo(String handoverInfo) { this.handoverInfo = handoverInfo; }
    public String getProcessingInfo() { return processingInfo; }
    public void setProcessingInfo(String processingInfo) { this.processingInfo = processingInfo; }
}
