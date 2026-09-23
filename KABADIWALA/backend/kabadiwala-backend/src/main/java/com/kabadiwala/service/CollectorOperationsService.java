package com.kabadiwala.service;

import com.kabadiwala.dto.PickupDto;
import com.kabadiwala.entity.*;
import com.kabadiwala.exception.InvalidTransactionException;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.*;
import com.kabadiwala.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
