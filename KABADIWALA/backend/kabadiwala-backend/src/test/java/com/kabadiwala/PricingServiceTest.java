package com.kabadiwala;

import com.kabadiwala.dto.PriceCalculationResponse;
import com.kabadiwala.entity.WasteCategory;
import com.kabadiwala.entity.WastePricing;
import com.kabadiwala.exception.InvalidTransactionException;
import com.kabadiwala.repository.WasteCategoryRepository;
import com.kabadiwala.repository.WastePricingRepository;
import com.kabadiwala.service.PricingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class PricingServiceTest {

    private WastePricingRepository pricingRepository;
    private WasteCategoryRepository categoryRepository;
    private PricingService pricingService;

    private WasteCategory plasticCategory;
    private WastePricing plasticPricing;

    @BeforeEach
    void setUp() {
        pricingRepository = Mockito.mock(WastePricingRepository.class);
        categoryRepository = Mockito.mock(WasteCategoryRepository.class);
        pricingService = new PricingService(pricingRepository, categoryRepository);

        plasticCategory = new WasteCategory();
        plasticCategory.setId(1L);
        plasticCategory.setName("Plastic");
        plasticCategory.setActive(true);

        plasticPricing = new WastePricing();
        plasticPricing.setId(10L);
        plasticPricing.setCategory(plasticCategory);
        plasticPricing.setRatePerKg(new BigDecimal("15.50"));
        plasticPricing.setActive(true);
    }

    @Test
    @DisplayName("Should correctly calculate final amount using BigDecimal HALF_UP rounding")
    void shouldCalculateFinalAmountCorrectly() {
        when(pricingRepository.findTopByCategoryIdAndActiveTrueOrderByEffectiveFromDesc(1L))
                .thenReturn(Optional.of(plasticPricing));

        BigDecimal weight = new BigDecimal("4.325");
        BigDecimal expectedAmount = weight.multiply(new BigDecimal("15.50")).setScale(2, java.math.RoundingMode.HALF_UP);

        BigDecimal calculated = pricingService.calculateFinalAmount(plasticCategory, weight);

        assertNotNull(calculated);
        assertEquals(expectedAmount, calculated);
        assertEquals(new BigDecimal("67.04"), calculated);
    }

    @Test
    @DisplayName("Should reject zero or negative weights")
    void shouldRejectZeroOrNegativeWeights() {
        assertThrows(InvalidTransactionException.class, () ->
                pricingService.calculateFinalAmount(plasticCategory, BigDecimal.ZERO));

        assertThrows(InvalidTransactionException.class, () ->
                pricingService.calculateFinalAmount(plasticCategory, new BigDecimal("-2.5")));
    }

    @Test
    @DisplayName("Should reject calculation for inactive waste category")
    void shouldRejectInactiveCategory() {
        plasticCategory.setActive(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(plasticCategory));

        assertThrows(InvalidTransactionException.class, () ->
                pricingService.calculateFinalPrice(1L, new BigDecimal("5.0")));
    }
}
