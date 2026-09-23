package com.kabadiwala.service;

import com.kabadiwala.dto.VerificationDto;
import com.kabadiwala.dto.VerificationRequest;
import com.kabadiwala.entity.*;
import com.kabadiwala.exception.InvalidTransactionException;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.*;
import com.kabadiwala.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class VerificationService {

    private final WasteVerificationRepository verificationRepository;
    private final PickupRepository pickupRepository;
    private final WasteCategoryRepository categoryRepository;
    private final WasteRepository wasteRepository;
    private final CollectorRepository collectorRepository;
    private final PricingService pricingService;
    private final TransactionService transactionService;
    private final PickupStatusRepository statusRepository;
    private final NotificationService notificationService;

    public VerificationService(WasteVerificationRepository verificationRepository,
                                PickupRepository pickupRepository,
                                WasteCategoryRepository categoryRepository,
                                WasteRepository wasteRepository,
                                CollectorRepository collectorRepository,
                                PricingService pricingService,
                                TransactionService transactionService,
                                PickupStatusRepository statusRepository,
                                NotificationService notificationService) {
        this.verificationRepository = verificationRepository;
        this.pickupRepository = pickupRepository;
        this.categoryRepository = categoryRepository;
        this.wasteRepository = wasteRepository;
        this.collectorRepository = collectorRepository;
        this.pricingService = pricingService;
        this.transactionService = transactionService;
        this.statusRepository = statusRepository;
        this.notificationService = notificationService;
    }

    /**
     * Collector verifies the pickup — actual weight replaces AI estimate.
     * Final amount = actualWeight × configuredRatePerKg (server-side — no client pricing trust).
     */
    @Transactional
    public VerificationDto verifyPickup(VerificationRequest request) {
        User collector = SecurityUtils.getCurrentUser();

        Pickup pickup = pickupRepository.findById(request.getPickupId())
                .orElseThrow(() -> new ResourceNotFoundException("Pickup", "id", request.getPickupId()));

        // Only COLLECTED pickups can be verified
        if (!Pickup.Status.COLLECTED.equals(pickup.getStatus())) {
            throw new InvalidTransactionException(
                    "Pickup must be in COLLECTED state to verify. Current: " + pickup.getStatus());
        }

        // Ensure assigned collector is the one verifying
        Collector collectorEntity = collectorRepository.findByUserId(collector.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Collector profile", "userId", collector.getId()));

        if (pickup.getAssignedCollector() == null ||
                !pickup.getAssignedCollector().getId().equals(collectorEntity.getId())) {
            throw new InvalidTransactionException("Only the assigned collector can verify this pickup.");
        }

        // Double-verification guard
        if (verificationRepository.existsByPickupId(pickup.getId())) {
            throw new InvalidTransactionException("Pickup #" + pickup.getId() + " has already been verified.");
        }

        WasteCategory actualCategory = categoryRepository.findById(request.getActualCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("WasteCategory", "id", request.getActualCategoryId()));

        Waste actualItem = null;
        if (request.getActualWasteItemId() != null) {
            actualItem = wasteRepository.findById(request.getActualWasteItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("WasteItem", "id", request.getActualWasteItemId()));
        }

        // === BUSINESS RULE: Final amount is ALWAYS server-side ===
        BigDecimal finalAmount = pricingService.calculateFinalAmount(actualCategory, request.getActualWeight());

        // Save verification record
        WasteVerification verification = new WasteVerification();
        verification.setPickup(pickup);
        verification.setCollector(collectorEntity);
        verification.setActualCategory(actualCategory);
        verification.setActualWasteItem(actualItem);
        verification.setActualWeight(request.getActualWeight());
        verification.setCondition(request.getCondition());
        verification.setNotes(request.getNotes());
        WasteVerification savedVerification = verificationRepository.save(verification);

        // Update pickup status → COMPLETED
        String oldStatus = pickup.getStatus().name();
        pickup.setStatus(Pickup.Status.COMPLETED);
        pickupRepository.save(pickup);

        statusRepository.save(new PickupStatusHistory(pickup, oldStatus, "COMPLETED",
                collector, "Verification completed by collector"));

        // Process Transaction, Payment, Wallet credit, and initial Recycling record
        transactionService.processVerifiedPickup(pickup, savedVerification);

        // Notify user
        notificationService.sendNotification(pickup.getUser(), "Pickup Completed & Payment Credited",
                "Your pickup has been verified. ₹" + finalAmount + " credited to your wallet.",
                "PAYMENT_CREDITED");

        return mapToDto(savedVerification, finalAmount);
    }

    @Transactional(readOnly = true)
    public VerificationDto getVerificationByPickupId(Long pickupId) {
        WasteVerification v = verificationRepository.findByPickupId(pickupId)
                .orElseThrow(() -> new ResourceNotFoundException("Verification", "pickupId", pickupId));
        BigDecimal finalAmount = pricingService.calculateFinalAmount(v.getActualCategory(), v.getActualWeight());
        return mapToDto(v, finalAmount);
    }

    private VerificationDto mapToDto(WasteVerification v, BigDecimal finalAmount) {
        VerificationDto dto = new VerificationDto();
        dto.setId(v.getId());
        dto.setPickupId(v.getPickup().getId());
        dto.setCollectorId(v.getCollector().getId());
        dto.setCollectorName(v.getCollector().getUser().getName());
        dto.setActualCategoryId(v.getActualCategory().getId());
        dto.setActualCategoryName(v.getActualCategory().getName());
        if (v.getActualWasteItem() != null) {
            dto.setActualWasteItemId(v.getActualWasteItem().getId());
            dto.setActualWasteItemName(v.getActualWasteItem().getName());
        }
        dto.setActualWeight(v.getActualWeight());
        dto.setFinalAmount(finalAmount);
        dto.setCondition(v.getCondition());
        dto.setNotes(v.getNotes());
        dto.setVerifiedAt(v.getVerifiedAt());
        return dto;
    }
}
