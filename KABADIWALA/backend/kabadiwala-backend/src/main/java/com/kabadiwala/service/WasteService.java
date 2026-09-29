package com.kabadiwala.service;

import com.kabadiwala.entity.WasteCategory;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.WasteCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WasteService {

    private final WasteCategoryRepository wasteCategoryRepository;

    public WasteService(WasteCategoryRepository wasteCategoryRepository) {
        this.wasteCategoryRepository = wasteCategoryRepository;
    }

    public List<WasteCategory> getAllCategories() {
        return wasteCategoryRepository.findByActiveTrue();
    }

    public Map<String, Object> estimatePrice(String categoryName, Double weightKg) {
        if (weightKg == null || weightKg <= 0) {
            weightKg = 1.0;
        }

        WasteCategory category = wasteCategoryRepository.findByNameIgnoreCase(categoryName)
                .or(() -> wasteCategoryRepository.findByCodeIgnoreCase(categoryName))
                .orElse(null);

        double rate = category != null ? category.getRatePerKg() : 15.0;
        double co2Factor = category != null ? category.getCo2FactorPerKg() : 1.5;

        double estimatedAmount = Math.round(rate * weightKg * 100.0) / 100.0;
        double estimatedCo2Saved = Math.round(co2Factor * weightKg * 100.0) / 100.0;
        int pointsEarned = (int) Math.round(weightKg * 10);

        Map<String, Object> result = new HashMap<>();
        result.put("categoryName", category != null ? category.getName() : categoryName);
        result.put("weightKg", weightKg);
        result.put("ratePerKg", rate);
        result.put("estimatedAmount", estimatedAmount);
        result.put("estimatedPoints", pointsEarned);
        result.put("co2SavedKg", estimatedCo2Saved);
        result.put("currency", "INR");
        return result;
    }
}
