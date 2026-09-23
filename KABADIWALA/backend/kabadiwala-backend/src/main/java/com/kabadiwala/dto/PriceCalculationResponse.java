package com.kabadiwala.dto;

import java.math.BigDecimal;

public class PriceCalculationResponse {
    private Long categoryId;
    private String categoryName;
    private BigDecimal weight;
    private BigDecimal ratePerKg;
    private BigDecimal finalAmount;

    public PriceCalculationResponse() {}

    public PriceCalculationResponse(Long categoryId, String categoryName, BigDecimal weight,
                                    BigDecimal ratePerKg, BigDecimal finalAmount) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.weight = weight;
        this.ratePerKg = ratePerKg;
        this.finalAmount = finalAmount;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getRatePerKg() {
        return ratePerKg;
    }

    public void setRatePerKg(BigDecimal ratePerKg) {
        this.ratePerKg = ratePerKg;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }
}
