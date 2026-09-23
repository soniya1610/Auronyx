package com.kabadiwala.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class AIAnalysisRequest {

    @NotBlank(message = "Image URL or base64 data is required")
    private String imageData;

    private String imageType; // "URL" or "BASE64"

    private Long pickupId;

    private Long wasteCategoryId;

    @Positive(message = "Estimated weight must be positive")
    private BigDecimal estimatedWeight;

    private String additionalContext;

    // Getters and Setters
    public String getImageData() { return imageData; }
    public void setImageData(String imageData) { this.imageData = imageData; }
    public String getImageType() { return imageType; }
    public void setImageType(String imageType) { this.imageType = imageType; }
    public Long getPickupId() { return pickupId; }
    public void setPickupId(Long pickupId) { this.pickupId = pickupId; }
    public Long getWasteCategoryId() { return wasteCategoryId; }
    public void setWasteCategoryId(Long wasteCategoryId) { this.wasteCategoryId = wasteCategoryId; }
    public BigDecimal getEstimatedWeight() { return estimatedWeight; }
    public void setEstimatedWeight(BigDecimal estimatedWeight) { this.estimatedWeight = estimatedWeight; }
    public String getAdditionalContext() { return additionalContext; }
    public void setAdditionalContext(String additionalContext) { this.additionalContext = additionalContext; }
}
