package com.kabadiwala.service;

import com.kabadiwala.entity.*;
import com.kabadiwala.exception.ForbiddenException;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.*;
import com.kabadiwala.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class PickupService {

    private final PickupRepository pickupRepository;
    private final UserRepository userRepository;
    private final CollectorRepository collectorRepository;
    private final WasteCategoryRepository wasteCategoryRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final NotificationService notificationService;

    public PickupService(
            PickupRepository pickupRepository,
            UserRepository userRepository,
            CollectorRepository collectorRepository,
            WasteCategoryRepository wasteCategoryRepository,
            WalletTransactionRepository walletTransactionRepository,
            NotificationService notificationService
    ) {
        this.pickupRepository = pickupRepository;
        this.userRepository = userRepository;
        this.collectorRepository = collectorRepository;
        this.wasteCategoryRepository = wasteCategoryRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.notificationService = notificationService;
    }

    private User getAuthenticatedUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    private Collector getAuthenticatedCollector() {
        User user = getAuthenticatedUser();
        return collectorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ForbiddenException("Collector profile not found for authenticated user"));
    }

    @Transactional
    public Pickup bookPickup(Map<String, Object> request) {
        User user = getAuthenticatedUser();

        String categoryName = request.getOrDefault("categoryName", "Mixed Recyclables").toString();
        String scheduledDate = request.getOrDefault("scheduledDate", "Today").toString();
        String timeSlot = request.getOrDefault("timeSlot", "10:00 AM - 12:00 PM").toString();
        String address = request.getOrDefault("address", user.getAddress() != null ? user.getAddress() : "Home").toString();
        String city = request.getOrDefault("city", user.getCity() != null ? user.getCity() : "City").toString();
        String pincode = request.getOrDefault("pincode", user.getPincode() != null ? user.getPincode() : "400001").toString();
        String notes = request.containsKey("notes") ? request.get("notes").toString() : "";

        double weight = 2.0;
        if (request.containsKey("estimatedWeightKg")) {
            weight = Double.parseDouble(request.get("estimatedWeightKg").toString());
        }

        double rate = 15.0;
        Optional<WasteCategory> catOpt = wasteCategoryRepository.findByNameIgnoreCase(categoryName);
        if (catOpt.isPresent()) {
            rate = catOpt.get().getRatePerKg();
        }

        double estimatedAmount = Math.round(rate * weight * 100.0) / 100.0;
        String verificationCode = String.valueOf(1000 + new Random().nextInt(9000));

        Pickup pickup = new Pickup();
        pickup.setUser(user);
        pickup.setCategoryName(categoryName);
        pickup.setStatus("REQUESTED");
        pickup.setScheduledDate(scheduledDate);
        pickup.setTimeSlot(timeSlot);
        pickup.setAddress(address);
        pickup.setCity(city);
        pickup.setPincode(pincode);
        pickup.setNotes(notes);
        pickup.setEstimatedWeightKg(weight);
        pickup.setEstimatedAmount(estimatedAmount);
        pickup.setVerificationCode(verificationCode);

        // If a preferred collector is specified in the request
        if (request.containsKey("collectorId") && request.get("collectorId") != null) {
            try {
                Long collectorId = Long.parseLong(request.get("collectorId").toString());
                collectorRepository.findById(collectorId).ifPresent(collector -> {
                    pickup.setCollector(collector);
                    pickup.setStatus("ASSIGNED");
                });
            } catch (Exception ignored) {}
        }

        Pickup saved = pickupRepository.save(pickup);

        notificationService.createNotification(
                user,
                "Pickup Booked Successfully",
                "Your " + categoryName + " pickup has been scheduled for " + scheduledDate + " (" + timeSlot + "). Code: " + verificationCode,
                NotificationType.GENERAL
        );

        return saved;
    }

    public List<Pickup> getUserPickups() {
        User user = getAuthenticatedUser();
        return pickupRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public Pickup getPickupById(Long id) {
        return pickupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pickup not found with ID: " + id));
    }

    public List<Pickup> getCollectorPickups() {
        try {
            Collector collector = getAuthenticatedCollector();
            List<Pickup> assigned = pickupRepository.findByCollectorIdOrderByCreatedAtDesc(collector.getId());
            List<Pickup> open = pickupRepository.findByStatusOrderByCreatedAtDesc("REQUESTED");
            Set<Pickup> combined = new LinkedHashSet<>(assigned);
            combined.addAll(open);
            return new ArrayList<>(combined);
        } catch (Exception e) {
            // Fallback for demo: return all active pickups
            return pickupRepository.findAll();
        }
    }

    @Transactional
    public Pickup acceptPickup(Long id) {
        Pickup pickup = getPickupById(id);
        Collector collector = getAuthenticatedCollector();

        pickup.setCollector(collector);
        pickup.setStatus("ASSIGNED");
        Pickup saved = pickupRepository.save(pickup);

        notificationService.createNotification(
                pickup.getUser(),
                "Collector Assigned",
                "Collector " + collector.getUser().getName() + " has accepted your pickup and is on the way.",
                NotificationType.GENERAL
        );

        return saved;
    }

    @Transactional
    public Pickup verifyWeight(Long id, Double actualWeightKg) {
        Pickup pickup = getPickupById(id);
        if (actualWeightKg == null || actualWeightKg <= 0) {
            actualWeightKg = pickup.getEstimatedWeightKg() != null ? pickup.getEstimatedWeightKg() : 2.0;
        }

        double rate = 15.0;
        Optional<WasteCategory> catOpt = wasteCategoryRepository.findByNameIgnoreCase(pickup.getCategoryName());
        if (catOpt.isPresent()) {
            rate = catOpt.get().getRatePerKg();
        }

        double finalAmount = Math.round(rate * actualWeightKg * 100.0) / 100.0;
        pickup.setActualWeightKg(actualWeightKg);
        pickup.setFinalAmount(finalAmount);
        pickup.setStatus("WEIGHED");

        return pickupRepository.save(pickup);
    }

    @Transactional
    public Pickup completePickup(Long id, String paymentMethod) {
        Pickup pickup = getPickupById(id);
        pickup.setStatus("COMPLETED");
        pickup.setPaymentStatus("PAID");
        Pickup saved = pickupRepository.save(pickup);

        double amount = pickup.getFinalAmount() != null ? pickup.getFinalAmount() : (pickup.getEstimatedAmount() != null ? pickup.getEstimatedAmount() : 50.0);

        // Credit user wallet
        WalletTransaction tx = new WalletTransaction(
                pickup.getUser().getId(),
                amount,
                "CREDIT",
                "Scrap payout for pickup #" + pickup.getId() + " (" + pickup.getCategoryName() + ")",
                pickup.getId()
        );
        walletTransactionRepository.save(tx);

        notificationService.createNotification(
                pickup.getUser(),
                "Pickup Completed & Payout Credited",
                "₹" + amount + " has been credited to your wallet for recycling " + pickup.getCategoryName() + "!",
                NotificationType.SYSTEM
        );

        return saved;
    }
}
