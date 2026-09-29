package com.kabadiwala.controller;

import com.kabadiwala.dto.ApiResponse;
import com.kabadiwala.entity.Pickup;
import com.kabadiwala.entity.User;
import com.kabadiwala.entity.WasteCategory;
import com.kabadiwala.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final CollectorRepository collectorRepository;
    private final RecyclerRepository recyclerRepository;
    private final PickupRepository pickupRepository;
    private final WasteCategoryRepository wasteCategoryRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    public AdminController(
            UserRepository userRepository,
            CollectorRepository collectorRepository,
            RecyclerRepository recyclerRepository,
            PickupRepository pickupRepository,
            WasteCategoryRepository wasteCategoryRepository,
            WalletTransactionRepository walletTransactionRepository
    ) {
        this.userRepository = userRepository;
        this.collectorRepository = collectorRepository;
        this.recyclerRepository = recyclerRepository;
        this.pickupRepository = pickupRepository;
        this.wasteCategoryRepository = wasteCategoryRepository;
        this.walletTransactionRepository = walletTransactionRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPlatformStats() {
        long userCount = userRepository.count();
        long collectorCount = collectorRepository.count();
        long recyclerCount = recyclerRepository.count();
        long totalPickups = pickupRepository.count();
        long pendingPickups = pickupRepository.findByStatusOrderByCreatedAtDesc("REQUESTED").size();

        double totalKg = pickupRepository.findAll().stream()
                .mapToDouble(p -> p.getActualWeightKg() != null ? p.getActualWeightKg() : (p.getEstimatedWeightKg() != null ? p.getEstimatedWeightKg() : 0.0))
                .sum();

        double totalPayouts = walletTransactionRepository.findAll().stream()
                .filter(t -> "CREDIT".equalsIgnoreCase(t.getType()))
                .mapToDouble(t -> t.getAmount())
                .sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userCount);
        stats.put("totalCollectors", collectorCount);
        stats.put("totalRecyclers", recyclerCount);
        stats.put("totalPickups", totalPickups);
        stats.put("pendingPickups", pendingPickups);
        stats.put("totalWasteRecycledKg", Math.round(totalKg * 100.0) / 100.0);
        stats.put("totalPayoutsDisbursed", Math.round(totalPayouts * 100.0) / 100.0);
        stats.put("co2OffsetKg", Math.round(totalKg * 1.8 * 100.0) / 100.0);

        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(userRepository.findAll()));
    }

    @GetMapping("/pickups")
    public ResponseEntity<ApiResponse<List<Pickup>>> getAllPickups() {
        return ResponseEntity.ok(ApiResponse.success(pickupRepository.findAll()));
    }

    @PutMapping("/rates")
    public ResponseEntity<ApiResponse<WasteCategory>> updateRate(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(body.get("id").toString());
        Double newRate = Double.valueOf(body.get("ratePerKg").toString());

        WasteCategory category = wasteCategoryRepository.findById(id).orElseThrow();
        category.setRatePerKg(newRate);
        WasteCategory updated = wasteCategoryRepository.save(category);

        return ResponseEntity.ok(ApiResponse.success("Category scrap rate updated", updated));
    }

    @GetMapping("/fraud-alerts")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getFraudAlerts() {
        List<Map<String, Object>> alerts = new ArrayList<>();
        Map<String, Object> a1 = new HashMap<>();
        a1.put("id", 1);
        a1.put("severity", "LOW");
        a1.put("type", "Weight Discrepancy");
        a1.put("message", "Pickup #104 weight difference is +15% from estimated image AI analysis");
        a1.put("status", "RESOLVED");
        alerts.add(a1);

        Map<String, Object> a2 = new HashMap<>();
        a2.put("id", 2);
        a2.put("severity", "INFO");
        a2.put("type", "Rapid Bookings");
        a2.put("message", "User ID 3 created 4 bookings in 1 hour - normal seasonal bulk pickup verified");
        a2.put("status", "DISMISSED");
        alerts.add(a2);

        return ResponseEntity.ok(ApiResponse.success(alerts));
    }
}
