package com.kabadiwala;

import com.kabadiwala.entity.RecyclingRecord;
import com.kabadiwala.entity.WasteCategory;
import com.kabadiwala.exception.InvalidTransactionException;
import com.kabadiwala.repository.PickupRepository;
import com.kabadiwala.repository.RecyclerRepository;
import com.kabadiwala.repository.RecyclingRecordRepository;
import com.kabadiwala.service.RecyclingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RecyclingServiceTest {

    private RecyclingService recyclingService;

    @BeforeEach
    void setUp() {
        RecyclingRecordRepository recyclingRecordRepository = Mockito.mock(RecyclingRecordRepository.class);
        RecyclerRepository recyclerRepository = Mockito.mock(RecyclerRepository.class);
        PickupRepository pickupRepository = Mockito.mock(PickupRepository.class);
        recyclingService = new RecyclingService(recyclingRecordRepository, recyclerRepository, pickupRepository);
    }

    @Test
    @DisplayName("Valid sequential recycling transitions should succeed")
    void shouldAllowValidRecyclingTransitions() {
        assertDoesNotThrow(() -> RecyclingService.validateTransition(
                RecyclingRecord.Status.COLLECTED, RecyclingRecord.Status.SORTED));
        assertDoesNotThrow(() -> RecyclingService.validateTransition(
                RecyclingRecord.Status.SORTED, RecyclingRecord.Status.AGGREGATED));
        assertDoesNotThrow(() -> RecyclingService.validateTransition(
                RecyclingRecord.Status.AGGREGATED, RecyclingRecord.Status.TRANSPORT));
        assertDoesNotThrow(() -> RecyclingService.validateTransition(
                RecyclingRecord.Status.TRANSPORT, RecyclingRecord.Status.RECEIVED));
        assertDoesNotThrow(() -> RecyclingService.validateTransition(
                RecyclingRecord.Status.RECEIVED, RecyclingRecord.Status.PROCESSING));
        assertDoesNotThrow(() -> RecyclingService.validateTransition(
                RecyclingRecord.Status.PROCESSING, RecyclingRecord.Status.RECYCLED));
    }

    @Test
    @DisplayName("Invalid recycling status transitions should throw InvalidTransactionException")
    void shouldRejectInvalidRecyclingTransitions() {
        // Cannot jump directly from COLLECTED to RECYCLED
        assertThrows(InvalidTransactionException.class, () ->
                RecyclingService.validateTransition(RecyclingRecord.Status.COLLECTED, RecyclingRecord.Status.RECYCLED));

        // Cannot go backwards from RECYCLED
        assertThrows(InvalidTransactionException.class, () ->
                RecyclingService.validateTransition(RecyclingRecord.Status.RECYCLED, RecyclingRecord.Status.PROCESSING));
    }

    @Test
    @DisplayName("Should accurately calculate CO2 emissions saved based on waste category")
    void shouldCalculateCO2SavedCorrectly() {
        WasteCategory plastic = new WasteCategory();
        plastic.setName("Plastic Bottles");
        BigDecimal plasticCO2 = recyclingService.calculateCO2Saved(plastic, new BigDecimal("10.000"));
        // 10.000 * 1.50 = 15.000
        assertEquals(new BigDecimal("15.000"), plasticCO2);

        WasteCategory metal = new WasteCategory();
        metal.setName("Metal & Scrap");
        BigDecimal metalCO2 = recyclingService.calculateCO2Saved(metal, new BigDecimal("5.000"));
        // 5.000 * 2.50 = 12.500
        assertEquals(new BigDecimal("12.500"), metalCO2);

        WasteCategory ewaste = new WasteCategory();
        ewaste.setName("E-Waste");
        BigDecimal ewasteCO2 = recyclingService.calculateCO2Saved(ewaste, new BigDecimal("2.000"));
        // 2.000 * 3.00 = 6.000
        assertEquals(new BigDecimal("6.000"), ewasteCO2);
    }
}
