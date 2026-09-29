package com.kabadiwala.service;

import com.kabadiwala.dto.CollectorAvailabilityRequest;
import com.kabadiwala.dto.CollectorProfileUpdateRequest;
import com.kabadiwala.dto.CollectorResponse;
import com.kabadiwala.entity.Collector;
import com.kabadiwala.entity.User;
import com.kabadiwala.exception.BadRequestException;
import com.kabadiwala.exception.ForbiddenException;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.CollectorRepository;
import com.kabadiwala.repository.UserRepository;
import com.kabadiwala.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollectorService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    private final CollectorRepository collectorRepository;
    private final UserRepository userRepository;

    public CollectorService(CollectorRepository collectorRepository, UserRepository userRepository) {
        this.collectorRepository = collectorRepository;
        this.userRepository = userRepository;
    }

    public Collector getAuthenticatedCollector() {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return collectorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ForbiddenException("Collector profile does not exist for user: " + email));
    }

    public CollectorResponse getProfile() {
        Collector collector = getAuthenticatedCollector();
        return mapToCollectorResponse(collector);
    }

    @Transactional
    public CollectorResponse updateProfile(CollectorProfileUpdateRequest request) {
        Collector collector = getAuthenticatedCollector();

        if (request.getVehicleType() != null) collector.setVehicleType(request.getVehicleType().trim());
        if (request.getVehicleNumber() != null) collector.setVehicleNumber(request.getVehicleNumber().trim());
        if (request.getServiceArea() != null) collector.setServiceArea(request.getServiceArea().trim());
        if (request.getAddress() != null) collector.setAddress(request.getAddress().trim());
        if (request.getCity() != null) collector.setCity(request.getCity().trim());
        if (request.getState() != null) collector.setState(request.getState().trim());
        if (request.getPincode() != null) collector.setPincode(request.getPincode().trim());
        if (request.getLatitude() != null) collector.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) collector.setLongitude(request.getLongitude());
        if (request.getWorkingHours() != null) collector.setWorkingHours(request.getWorkingHours().trim());

        Collector updatedCollector = collectorRepository.save(collector);
        return mapToCollectorResponse(updatedCollector);
    }

    @Transactional
    public CollectorResponse updateAvailability(CollectorAvailabilityRequest request) {
        Collector collector = getAuthenticatedCollector();
        collector.setAvailable(request.getIsAvailable());
        if (request.getWorkingHours() != null) {
            collector.setWorkingHours(request.getWorkingHours().trim());
        }

        Collector updatedCollector = collectorRepository.save(collector);
        return mapToCollectorResponse(updatedCollector);
    }

    public List<CollectorResponse> getAllCollectors() {
        return collectorRepository.findAllByIsActiveTrue().stream()
                .map(this::mapToCollectorResponse)
                .collect(Collectors.toList());
    }

    public CollectorResponse getCollectorById(Long id) {
        Collector collector = collectorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collector not found with ID: " + id));

        if (!collector.isActive()) {
            throw new ResourceNotFoundException("Collector is inactive");
        }

        return mapToCollectorResponse(collector);
    }

    public List<CollectorResponse> getNearbyCollectors(Double latitude, Double longitude, Double radiusKm) {
        double userLat;
        double userLng;
        double maxRadius = (radiusKm != null && radiusKm > 0) ? radiusKm : 10.0;

        if (latitude != null && longitude != null) {
            userLat = latitude;
            userLng = longitude;
        } else if (SecurityUtils.isAuthenticated()) {
            String email = SecurityUtils.getCurrentUserEmail();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
            if (user.getLatitude() == null || user.getLongitude() == null) {
                throw new BadRequestException("User location is not set. Please provide coordinates or update your profile location.");
            }
            userLat = user.getLatitude();
            userLng = user.getLongitude();
        } else {
            throw new BadRequestException("Latitude and Longitude query parameters are required for unauthenticated proximity requests.");
        }

        List<Collector> availableCollectors = collectorRepository.findAllByIsActiveTrueAndIsAvailableTrue();

        return availableCollectors.stream()
                .filter(c -> c.getLatitude() != null && c.getLongitude() != null)
                .map(c -> {
                    double dist = calculateHaversineDistance(userLat, userLng, c.getLatitude(), c.getLongitude());
                    CollectorResponse resp = mapToCollectorResponse(c);
                    resp.setDistanceKm(round(dist, 2));
                    return resp;
                })
                .filter(resp -> resp.getDistanceKm() <= maxRadius)
                .sorted(Comparator.comparing(CollectorResponse::getDistanceKm))
                .collect(Collectors.toList());
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public CollectorResponse mapToCollectorResponse(Collector collector) {
        CollectorResponse response = new CollectorResponse();
        response.setId(collector.getId());
        if (collector.getUser() != null) {
            response.setUserId(collector.getUser().getId());
            response.setName(collector.getUser().getName());
            response.setPhone(collector.getUser().getPhone());
        }
        response.setVehicleType(collector.getVehicleType());
        response.setVehicleNumber(collector.getVehicleNumber());
        response.setServiceArea(collector.getServiceArea());
        response.setAddress(collector.getAddress());
        response.setCity(collector.getCity());
        response.setState(collector.getState());
        response.setPincode(collector.getPincode());
        response.setLatitude(collector.getLatitude());
        response.setLongitude(collector.getLongitude());
        response.setAvailable(collector.isAvailable());
        response.setActive(collector.isActive());
        response.setVerificationStatus(collector.getVerificationStatus());
        response.setWorkingHours(collector.getWorkingHours());
        response.setCreatedAt(collector.getCreatedAt());
        response.setUpdatedAt(collector.getUpdatedAt());
        return response;
    }
}
