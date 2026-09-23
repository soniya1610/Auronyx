package com.kabadiwala.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class WasteRequest {

    @NotBlank(message = "Waste item name is required")
    private String name;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private String description;
    private String unit;
    private String materialType;
    private Boolean recyclable;
    private Boolean hazardous;
    private String imageUrl;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getMaterialType() { return materialType; }
    public void setMaterialType(String materialType) { this.materialType = materialType; }
    public Boolean getRecyclable() { return recyclable; }
    public void setRecyclable(Boolean recyclable) { this.recyclable = recyclable; }
    public Boolean getHazardous() { return hazardous; }
    public void setHazardous(Boolean hazardous) { this.hazardous = hazardous; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
