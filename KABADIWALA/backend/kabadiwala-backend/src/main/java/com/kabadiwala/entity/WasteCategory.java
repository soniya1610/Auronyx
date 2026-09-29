package com.kabadiwala.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "waste_categories")
public class WasteCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String code;

    private String description;

    @Column(nullable = false)
    private Double ratePerKg;

    private String unit = "kg";

    private String icon;

    private Double co2FactorPerKg = 1.5;

    private boolean active = true;

    public WasteCategory() {
    }

    public WasteCategory(String name, String code, String description, Double ratePerKg, String unit, String icon, Double co2FactorPerKg) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.ratePerKg = ratePerKg;
        this.unit = unit;
        this.icon = icon;
        this.co2FactorPerKg = co2FactorPerKg;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRatePerKg() {
        return ratePerKg;
    }

    public void setRatePerKg(Double ratePerKg) {
        this.ratePerKg = ratePerKg;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Double getCo2FactorPerKg() {
        return co2FactorPerKg;
    }

    public void setCo2FactorPerKg(Double co2FactorPerKg) {
        this.co2FactorPerKg = co2FactorPerKg;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
