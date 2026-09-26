package com.kabadiwala.service;

import com.kabadiwala.dto.PickupDto;
import com.kabadiwala.entity.*;
import com.kabadiwala.exception.InvalidTransactionException;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.*;
import com.kabadiwala.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollectorOperationsService {

    private final CollectorRepository collectorRepository;
    private final PickupRepository pickupRepository;
    private final PickupStatusRepository statusRepository;
    private final NotificationService notificationService;
    private final PickupService pickupService;

    public CollectorOperationsService(CollectorRepository collectorRepository,
                                       PickupRepository pickupRepository,
                                       PickupStatusRepository statusRepository,
                                       NotificationService notificationService,
                                       PickupService pickupService) {
        this.collectorRepository = collectorRepository;
        this.pickupRepository = pickupRepository;
        this.statusRepository = statusRepository;
        this.notificationService = notificationService;
        this.pickupService = pickupService;
    }

    /**
     * Haversine distance in kilometers between two geo-coordinates.
     */
    public static double calculateHaversineDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round((R * c) * 100.0) / 100.0;
    }

    /** Returns all REQUESTED pickups in the collector's city that are unassigned */
    @Transactional(readOnly = true)
    public List<PickupDto> getAvailablePickups() {
        Collector collector = getCurrentCollector();
        List<Pickup> pickups = pickupRepository.findByCityIgnoreCaseAndStatusIn(
                collector.getServiceArea(),
                List.of(Pickup.Status.REQUESTED));
        return pickups.stream()
                .filter(p -> p.getAssignedCollector() == null)
                .map(pickupService::mapToDto)
                .collect(Collectors.toList());
    }

    /** Returns pickups assigned to this collector */
    @Transactional(readOnly = true)
    public List<PickupDto> getMyAssignedPickups() {
        Collector collector = getCurrentCollector();
        return pickupRepository.findByAssignedCollectorIdOrderByCreatedAtDesc(collector.getId())
                .stream().map(pickupService::mapToDto).collect(Collectors.toList());
    }

    /** Returns pickups by optional status */
    @Transactional(readOnly = true)
    public List<PickupDto> getPickups(String status) {
        if (status == null || status.isBlank() || status.equalsIgnoreCase("AVAILABLE")) {
            return getAvailablePickups();
        }
        if (status.equalsIgnoreCase("ASSIGNED")) {
            return getMyAssignedPickups();
        }
        Collector collector = getCurrentCollector();
        try {
            Pickup.Status targetStatus = Pickup.Status.valueOf(status.toUpperCase());
            return pickupRepository.findByAssignedCollectorIdOrderByCreatedAtDesc(collector.getId())
                    .stream()
                    .filter(p -> p.getStatus() == targetStatus)
                    .map(pickupService::mapToDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return getAvailablePickups();
        }
    }

    /**
     * Returns nearby unassigned REQUESTED pickups within radiusKm,
     * using exact Haversine distance based on actual stored coordinates.
     */
    @Transactional(readOnly = true)
    public List<PickupDto> getNearbyPickups(Double latitude, Double longitude, Double radiusKm) {
        Collector collector = getCurrentCollector();

        double refLat;
        double refLon;
        if (latitude != null && longitude != null) {
            refLat = latitude;
            refLon = longitude;
        } else if (collector.getLatitude() != null && collector.getLongitude() != null) {
            refLat = collector.getLatitude();
            refLon = collector.getLongitude();
        } else {
            // If collector has serviceArea set, fallback to serviceArea pickups
            return getAvailablePickups();
        }

        double maxRadius = (radiusKm != null && radiusKm > 0) ? radiusKm : 25.0;

        List<Pickup> allRequested = pickupRepository.findAll().stream()
                .filter(p -> p.getStatus() == Pickup.Status.REQUESTED && p.getAssignedCollector() == null)
                .filter(p -> p.getLatitude() != null && p.getLongitude() != null)
                .collect(Collectors.toList());

        return allRequested.stream()
                .map(p -> {
                    double dist = calculateHaversineDistanceKm(refLat, refLon, p.getLatitude(), p.getLongitude());
                    PickupDto dto = pickupService.mapToDto(p);
                    dto.setDistanceKm(dist);
                    return dto;
                })
                .filter(dto -> dto.getDistanceKm() <= maxRadius)
                .sorted(Comparator.comparingDouble(PickupDto::getDistanceKm))
                .collect(Collectors.toList());
    }

    /** Accept a pickup: REQUESTED → ACCEPTED */
    @Transactional
    public PickupDto acceptPickup(Long pickupId) {
        Collector collector = getCurrentCollector();
        Pickup pickup = getPickupById(pickupId);
        PickupService.validateTransition(pickup.getStatus(), Pickup.Status.ACCEPTED);

        if (pickup.getAssignedCollector() != null) {
            throw new InvalidTransactionException("Pickup #" + pickupId + " is already assigned.");
        }

        String oldStatus = pickup.getStatus().name();
        pickup.setAssignedCollector(collector);
        pickup.setStatus(Pickup.Status.ACCEPTED);
        Pickup saved = pickupRepository.save(pickup);

        User collectorUser = SecurityUtils.getCurrentUser();
        statusRepository.save(new PickupStatusHistory(saved, oldStatus, "ACCEPTED", collectorUser,
                "Accepted by collector"));

        notificationService.sendNotification(pickup.getUser(), "Pickup Accepted",
                "Your pickup has been accepted by a collector.", "PICKUP_ACCEPTED");

        return pickupService.mapToDto(saved);
    }

    /** Reject / Unassign pickup */
    @Transactional
    public PickupDto rejectPickup(Long pickupId, String reason) {
        Collector collector = getCurrentCollector();
        Pickup pickup = getAndVerifyAssigned(pickupId, collector);

        if (pickup.getStatus() != Pickup.Status.ACCEPTED && pickup.getStatus() != Pickup.Status.REQUESTED) {
            throw new InvalidTransactionException("Cannot reject pickup currently in status: " + pickup.getStatus());
        }

        String oldStatus = pickup.getStatus().name();
        pickup.setAssignedCollector(null);
        pickup.setStatus(Pickup.Status.REQUESTED);
        Pickup saved = pickupRepository.save(pickup);

        User collectorUser = SecurityUtils.getCurrentUser();
        String remarks = (reason != null && !reason.isBlank()) ? "Rejected by collector: " + reason : "Rejected/released by collector";
        statusRepository.save(new PickupStatusHistory(saved, oldStatus, "REQUESTED", collectorUser, remarks));

        notificationService.sendNotification(pickup.getUser(), "Pickup Unassigned",
                "Your pickup has been made available to other collectors.", "PICKUP_REOPENED");

        return pickupService.mapToDto(saved);
    }

    /** Mark on the way: ACCEPTED → ON_THE_WAY */
    @Transactional
    public PickupDto markOnTheWay(Long pickupId) {
        Collector collector = getCurrentCollector();
        Pickup pickup = getAndVerifyAssigned(pickupId, collector);
        PickupService.validateTransition(pickup.getStatus(), Pickup.Status.ON_THE_WAY);

        String oldStatus = pickup.getStatus().name();
        pickup.setStatus(Pickup.Status.ON_THE_WAY);
        Pickup saved = pickupRepository.save(pickup);

        statusRepository.save(new PickupStatusHistory(saved, oldStatus, "ON_THE_WAY",
                SecurityUtils.getCurrentUser(), "Collector on the way"));

        notificationService.sendNotification(pickup.getUser(), "Collector On The Way",
                "Your collector is on the way to collect the waste.", "COLLECTOR_ON_THE_WAY");

        return pickupService.mapToDto(saved);
    }

    /** Mark collected: ON_THE_WAY → COLLECTED */
    @Transactional
    public PickupDto markCollected(Long pickupId) {
        Collector collector = getCurrentCollector();
        Pickup pickup = getAndVerifyAssigned(pickupId, collector);
        PickupService.validateTransition(pickup.getStatus(), Pickup.Status.COLLECTED);

        String oldStatus = pickup.getStatus().name();
        pickup.setStatus(Pickup.Status.COLLECTED);
        Pickup saved = pickupRepository.save(pickup);

        statusRepository.save(new PickupStatusHistory(saved, oldStatus, "COLLECTED",
                SecurityUtils.getCurrentUser(), "Waste physically collected"));

        notificationService.sendNotification(pickup.getUser(), "Waste Collected",
                "Your waste has been collected. Verification and payment will follow shortly.",
                "WASTE_COLLECTED");

        return pickupService.mapToDto(saved);
    }

    /** General status update with validation */
    @Transactional
    public PickupDto updateStatus(Long pickupId, String statusName) {
        if (statusName == null || statusName.isBlank()) {
            throw new InvalidTransactionException("Target status name is required.");
        }
        String upper = statusName.trim().toUpperCase();
        if (upper.equals("ON_THE_WAY")) {
            return markOnTheWay(pickupId);
        } else if (upper.equals("COLLECTED")) {
            return markCollected(pickupId);
        } else {
            throw new InvalidTransactionException("Status update via this endpoint supports ON_THE_WAY or COLLECTED. Provided: " + statusName);
        }
    }

    // -------- helpers --------

    private Collector getCurrentCollector() {
        Long userId = SecurityUtils.getCurrentUserId();
        return collectorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Collector profile", "userId", userId));
    }

    private Pickup getPickupById(Long id) {
        return pickupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pickup", "id", id));
    }

    private Pickup getAndVerifyAssigned(Long pickupId, Collector collector) {
        Pickup pickup = getPickupById(pickupId);
        if (pickup.getAssignedCollector() == null ||
                !pickup.getAssignedCollector().getId().equals(collector.getId())) {
            throw new InvalidTransactionException("You are not assigned to pickup #" + pickupId);
        }
        return pickup;
    }
}
