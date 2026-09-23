package com.kabadiwala.service;

import com.kabadiwala.dto.PickupDto;
import com.kabadiwala.dto.PickupRequest;
import com.kabadiwala.entity.*;
import com.kabadiwala.exception.InvalidTransactionException;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.exception.UnauthorizedException;
import com.kabadiwala.repository.*;
import com.kabadiwala.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PickupService {

    private final PickupRepository pickupRepository;
    private final PickupStatusRepository statusRepository;
    private final WasteCategoryRepository categoryRepository;
    private final WasteRepository wasteRepository;
    private final NotificationService notificationService;

    public PickupService(PickupRepository pickupRepository,
                         PickupStatusRepository statusRepository,
                         WasteCategoryRepository categoryRepository,
                         WasteRepository wasteRepository,
                         NotificationService notificationService) {
        this.pickupRepository = pickupRepository;
        this.statusRepository = statusRepository;
        this.categoryRepository = categoryRepository;
        this.wasteRepository = wasteRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public PickupDto createPickup(PickupRequest request) {
        User currentUser = SecurityUtils.getCurrentUser();

        WasteCategory category = categoryRepository.findById(request.getWasteCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("WasteCategory", "id", request.getWasteCategoryId()));
        if (!Boolean.TRUE.equals(category.getActive())) {
            throw new IllegalArgumentException("Waste category is inactive: " + category.getName());
        }

        Waste wasteItem = null;
        if (request.getWasteItemId() != null) {
            wasteItem = wasteRepository.findById(request.getWasteItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("WasteItem", "id", request.getWasteItemId()));
        }

        Pickup pickup = new Pickup();
        pickup.setUser(currentUser);
        pickup.setWasteCategory(category);
        pickup.setWasteItem(wasteItem);
        pickup.setEstimatedQuantity(request.getEstimatedQuantity() != null ? request.getEstimatedQuantity() : 1);
        pickup.setEstimatedWeight(request.getEstimatedWeight());
        pickup.setAddressLine(request.getAddressLine());
        pickup.setCity(request.getCity());
        pickup.setState(request.getState());
        pickup.setPincode(request.getPincode());
        pickup.setLatitude(request.getLatitude());
        pickup.setLongitude(request.getLongitude());
        pickup.setScheduledDate(request.getScheduledDate());
        pickup.setScheduledTime(request.getScheduledTime());
        pickup.setNotes(request.getNotes());
        pickup.setStatus(Pickup.Status.REQUESTED);

        Pickup saved = pickupRepository.save(pickup);

        // Record initial status history
        statusRepository.save(new PickupStatusHistory(saved, null, "REQUESTED", currentUser, "Pickup requested"));

        notificationService.sendNotification(currentUser, "Pickup Requested",
                "Your pickup request for " + category.getName() + " has been created.", "PICKUP_REQUESTED");

        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<PickupDto> getMyPickups() {
        Long userId = SecurityUtils.getCurrentUserId();
        return pickupRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PickupDto getPickupById(Long id) {
        Pickup pickup = findAndVerifyOwnership(id);
        return mapToDto(pickup);
    }

    @Transactional
    public PickupDto updatePickup(Long id, PickupRequest request) {
        Pickup pickup = findAndVerifyOwnership(id);

        if (!Pickup.Status.REQUESTED.equals(pickup.getStatus())) {
            throw new IllegalStateException("Only REQUESTED pickups can be updated. Current status: " + pickup.getStatus());
        }

        if (request.getWasteCategoryId() != null) {
            WasteCategory category = categoryRepository.findById(request.getWasteCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("WasteCategory", "id", request.getWasteCategoryId()));
            pickup.setWasteCategory(category);
        }
        if (request.getScheduledDate() != null) pickup.setScheduledDate(request.getScheduledDate());
        if (request.getScheduledTime() != null) pickup.setScheduledTime(request.getScheduledTime());
        if (request.getAddressLine() != null) pickup.setAddressLine(request.getAddressLine());
        if (request.getCity() != null) pickup.setCity(request.getCity());
        if (request.getState() != null) pickup.setState(request.getState());
        if (request.getPincode() != null) pickup.setPincode(request.getPincode());
        if (request.getNotes() != null) pickup.setNotes(request.getNotes());
        if (request.getEstimatedWeight() != null) pickup.setEstimatedWeight(request.getEstimatedWeight());
        if (request.getLatitude() != null) pickup.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) pickup.setLongitude(request.getLongitude());

        return mapToDto(pickupRepository.save(pickup));
    }

    @Transactional
    public void deletePickup(Long id) {
        Pickup pickup = findAndVerifyOwnership(id);
        if (pickup.getStatus() != Pickup.Status.REQUESTED && pickup.getStatus() != Pickup.Status.CANCELLED) {
            throw new IllegalStateException("Only REQUESTED or CANCELLED pickups can be deleted.");
        }
        pickupRepository.delete(pickup);
    }

    @Transactional
    public PickupDto cancelPickup(Long id) {
        Pickup pickup = findAndVerifyOwnership(id);
        validateTransition(pickup.getStatus(), Pickup.Status.CANCELLED);

        String oldStatus = pickup.getStatus().name();
        pickup.setStatus(Pickup.Status.CANCELLED);
        Pickup saved = pickupRepository.save(pickup);

        User currentUser = SecurityUtils.getCurrentUser();
        statusRepository.save(new PickupStatusHistory(saved, oldStatus, "CANCELLED", currentUser, "Cancelled by user"));
        notificationService.sendNotification(currentUser, "Pickup Cancelled", "Your pickup has been cancelled.", "PICKUP_CANCELLED");

        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public String getPickupStatus(Long id) {
        Pickup pickup = findAndVerifyOwnership(id);
        return pickup.getStatus().name();
    }

    // Called by Collector operations
    @Transactional
    public Pickup getPickupEntityById(Long id) {
        return pickupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pickup", "id", id));
    }

    public static void validateTransition(Pickup.Status from, Pickup.Status to) {
        boolean valid = switch (from) {
            case REQUESTED -> to == Pickup.Status.ACCEPTED || to == Pickup.Status.CANCELLED;
            case ACCEPTED -> to == Pickup.Status.ON_THE_WAY || to == Pickup.Status.CANCELLED;
            case ON_THE_WAY -> to == Pickup.Status.COLLECTED;
            case COLLECTED -> to == Pickup.Status.COMPLETED;
            default -> false;
        };
        if (!valid) {
            throw new InvalidTransactionException(
                    "Invalid pickup status transition: " + from + " → " + to);
        }
    }

    private Pickup findAndVerifyOwnership(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        Pickup pickup = pickupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pickup", "id", id));
        if (!pickup.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Access denied: you do not own this pickup.");
        }
        return pickup;
    }

    public PickupDto mapToDto(Pickup pickup) {
        PickupDto dto = new PickupDto();
        dto.setId(pickup.getId());
        dto.setUserId(pickup.getUser().getId());
        dto.setUserName(pickup.getUser().getName());
        dto.setWasteCategoryId(pickup.getWasteCategory().getId());
        dto.setWasteCategoryName(pickup.getWasteCategory().getName());
        if (pickup.getWasteItem() != null) {
            dto.setWasteItemId(pickup.getWasteItem().getId());
            dto.setWasteItemName(pickup.getWasteItem().getName());
        }
        dto.setEstimatedQuantity(pickup.getEstimatedQuantity());
        dto.setEstimatedWeight(pickup.getEstimatedWeight());
        dto.setAddressLine(pickup.getAddressLine());
        dto.setCity(pickup.getCity());
        dto.setState(pickup.getState());
        dto.setPincode(pickup.getPincode());
        dto.setLatitude(pickup.getLatitude());
        dto.setLongitude(pickup.getLongitude());
        dto.setScheduledDate(pickup.getScheduledDate());
        dto.setScheduledTime(pickup.getScheduledTime());
        dto.setNotes(pickup.getNotes());
        if (pickup.getAssignedCollector() != null) {
            dto.setAssignedCollectorId(pickup.getAssignedCollector().getId());
            dto.setAssignedCollectorName(pickup.getAssignedCollector().getUser().getName());
        }
        dto.setStatus(pickup.getStatus().name());
        dto.setCreatedAt(pickup.getCreatedAt());
        dto.setUpdatedAt(pickup.getUpdatedAt());
        return dto;
    }
}
