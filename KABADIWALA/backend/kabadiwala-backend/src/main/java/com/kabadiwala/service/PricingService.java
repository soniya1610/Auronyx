package com.kabadiwala.service;

import com.kabadiwala.dto.PriceCalculationResponse;
import com.kabadiwala.dto.WastePricingDto;
import com.kabadiwala.entity.WasteCategory;
import com.kabadiwala.entity.WastePricing;
import com.kabadiwala.exception.InvalidTransactionException;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.WasteCategoryRepository;
import com.kabadiwala.repository.WastePricingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PricingService {

    private final WastePricingRepository pricingRepository;
    private final WasteCategoryRepository categoryRepository;

    public PricingService(WastePricingRepository pricingRepository, WasteCategoryRepository categoryRepository) {
        this.pricingRepository = pricingRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<WastePricingDto> getAllActivePrices() {
        return pricingRepository.findByActiveTrue().stream()
                .map(this::mapPricingToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WastePricingDto getActivePricingByCategoryId(Long categoryId) {
        WastePricing pricing = pricingRepository
                .findTopByCategoryIdAndActiveTrueOrderByEffectiveFromDesc(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Active pricing not found for category ID: " + categoryId));
        return mapPricingToDto(pricing);
    }

    @Transactional(readOnly = true)
    public WastePricing getActivePricingEntityByCategoryId(Long categoryId) {
        return pricingRepository
                .findTopByCategoryIdAndActiveTrueOrderByEffectiveFromDesc(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Active pricing not found for category ID: " + categoryId));
    }

    @Transactional(readOnly = true)
    public PriceCalculationResponse calculateFinalPrice(Long categoryId, BigDecimal weight) {
        if (weight == null || weight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Actual weight must be positive and non-zero: " + weight);
        }

        WasteCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("WasteCategory", "id", categoryId));

        if (!Boolean.TRUE.equals(category.getActive())) {
            throw new InvalidTransactionException("Waste category is inactive: " + category.getName());
        }

        WastePricing pricing = getActivePricingEntityByCategoryId(categoryId);
        BigDecimal rate = pricing.getRatePerKg();

        // Calculate: weight * rate with scale 2 and RoundingMode.HALF_UP
        BigDecimal finalAmount = weight.multiply(rate).setScale(2, RoundingMode.HALF_UP);

        return new PriceCalculationResponse(
                category.getId(),
                category.getName(),
                weight,
                rate,
                finalAmount
        );
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateFinalAmount(WasteCategory category, BigDecimal weight) {
        if (weight == null || weight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Actual weight must be positive and non-zero: " + weight);
        }
        WastePricing pricing = getActivePricingEntityByCategoryId(category.getId());
        return weight.multiply(pricing.getRatePerKg()).setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional(readOnly = true)
    public BigDecimal getPriceForCategory(Long categoryId) {
        WastePricing pricing = getActivePricingEntityByCategoryId(categoryId);
        return pricing.getRatePerKg();
    }

    public WastePricingDto mapPricingToDto(WastePricing pricing) {
        return new WastePricingDto(
                pricing.getId(),
                pricing.getCategory().getId(),
                pricing.getCategory().getName(),
                pricing.getRatePerKg(),
                pricing.getActive(),
                pricing.getEffectiveFrom(),
                pricing.getEffectiveTo(),
                pricing.getCreatedAt(),
                pricing.getUpdatedAt()
        );
    }
}
