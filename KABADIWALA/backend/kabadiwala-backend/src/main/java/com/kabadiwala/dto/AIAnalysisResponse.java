package com.kabadiwala.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class AIAnalysisResponse {
    private Long id;
    private Long userId;
    private String category;
    private String item;
    private String condition;
    private BigDecimal estimatedWeight;
    private BigDecimal priceMin;
    private BigDecimal priceMax;
    private BigDecimal estimatedPrice;
    private BigDecimal confidence;
    private String imageUrl;
    private String provider;
    private String disclaimer = "AI estimation only. Final amount determined by physical collector verification.";
    private Map<String, Object> rawPrediction;
    private LocalDateTime createdAt = LocalDateTime.now();

    public AIAnalysisResponse() {}

    public AIAnalysisResponse(Long id, String category, String item, String condition,
                              BigDecimal estimatedWeight, BigDecimal priceMin, BigDecimal priceMax,
                              BigDecimal confidence, String imageUrl, LocalDateTime createdAt,
                              String provider) {
        this.id = id;
        this.category = category;
        this.item = item;
        this.condition = condition;
        this.estimatedWeight = estimatedWeight;
        this.priceMin = priceMin;
        this.priceMax = priceMax;
        this.confidence = confidence;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.provider = provider;
        if (priceMin != null && priceMax != null) {
            this.estimatedPrice = priceMin.add(priceMax).divide(BigDecimal.valueOf(2), 2, java.math.RoundingMode.HALF_UP);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getPredictedCategory() { return category; }
    public void setPredictedCategory(String predictedCategory) { this.category = predictedCategory; }

    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }
    public String getPredictedItem() { return item; }
    public void setPredictedItem(String predictedItem) { this.item = predictedItem; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public BigDecimal getEstimatedWeight() { return estimatedWeight; }
    public void setEstimatedWeight(BigDecimal estimatedWeight) { this.estimatedWeight = estimatedWeight; }

    public BigDecimal getPriceMin() { return priceMin; }
    public void setPriceMin(BigDecimal priceMin) { this.priceMin = priceMin; }

    public BigDecimal getPriceMax() { return priceMax; }
    public void setPriceMax(BigDecimal priceMax) { this.priceMax = priceMax; }

    public BigDecimal getEstimatedPrice() {
        if (estimatedPrice != null) return estimatedPrice;
        if (priceMin != null && priceMax != null) {
            return priceMin.add(priceMax).divide(BigDecimal.valueOf(2), 2, java.math.RoundingMode.HALF_UP);
        }
        return priceMin;
    }
    public void setEstimatedPrice(BigDecimal estimatedPrice) { this.estimatedPrice = estimatedPrice; }

    public BigDecimal getConfidence() { return confidence; }
    public void setConfidence(BigDecimal confidence) { this.confidence = confidence; }
    public void setConfidence(Double confidence) {
        this.confidence = confidence != null ? BigDecimal.valueOf(confidence) : null;
    }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getDisclaimer() { return disclaimer; }
    public void setDisclaimer(String disclaimer) { this.disclaimer = disclaimer; }

    public Map<String, Object> getRawPrediction() { return rawPrediction; }
    public void setRawPrediction(Map<String, Object> rawPrediction) { this.rawPrediction = rawPrediction; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getAnalyzedAt() { return createdAt; }
    public void setAnalyzedAt(LocalDateTime analyzedAt) { this.createdAt = analyzedAt; }
}
