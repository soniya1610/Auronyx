package com.kabadiwala.controller;

import com.kabadiwala.dto.ApiResponse;
import com.kabadiwala.dto.RecyclerProfileUpdateRequest;
import com.kabadiwala.dto.RecyclerResponse;
import com.kabadiwala.service.RecyclerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/recycler")
public class RecyclerController {

    private final RecyclerService recyclerService;

    public RecyclerController(RecyclerService recyclerService) {
        this.recyclerService = recyclerService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<RecyclerResponse>> getProfile() {
        RecyclerResponse response = recyclerService.getProfile();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<RecyclerResponse>> updateProfile(@Valid @RequestBody RecyclerProfileUpdateRequest request) {
        RecyclerResponse response = recyclerService.updateProfile(request);
        return ResponseEntity.ok(ApiResponse.success("Recycler profile updated successfully", response));
    }

    @GetMapping("/incoming")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getIncomingWaste() {
        // Returns list of incoming waste batches from verified collectors
        List<Map<String, Object>> batches = new ArrayList<>();
        Map<String, Object> b1 = new HashMap<>();
        b1.put("id", 101L);
        b1.put("batchCode", "BATCH-2026-0901");
        b1.put("collectorName", "Ramesh Kumar");
        b1.put("materialType", "Plastic (PET/HDPE)");
        b1.put("weightKg", 450.0);
        b1.put("status", "RECEIVED");
        b1.put("date", "2026-09-28");
        batches.add(b1);

        Map<String, Object> b2 = new HashMap<>();
        b2.put("id", 102L);
        b2.put("batchCode", "BATCH-2026-0902");
        b2.put("collectorName", "Sunil Verma");
        b2.put("materialType", "Cardboard / Mixed Paper");
        b2.put("weightKg", 820.0);
        b2.put("status", "PROCESSING");
        b2.put("date", "2026-09-29");
        batches.add(b2);

        Map<String, Object> b3 = new HashMap<>();
        b3.put("id", 103L);
        b3.put("batchCode", "BATCH-2026-0903");
        b3.put("collectorName", "Amit Patel");
        b3.put("materialType", "E-Waste Circuit Boards");
        b3.put("weightKg", 125.0);
        b3.put("status", "RECOVERED");
        b3.put("date", "2026-09-29");
        batches.add(b3);

        return ResponseEntity.ok(ApiResponse.success(batches));
    }

    @PostMapping("/batches")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createBatch(@RequestBody Map<String, Object> payload) {
        Map<String, Object> res = new HashMap<>(payload);
        res.put("id", System.currentTimeMillis());
        res.put("batchCode", "BATCH-" + System.currentTimeMillis() % 10000);
        res.put("status", "PROCESSING");
        res.put("recoveryRate", "89.5%");
        res.put("message", "Waste batch logged and queued for recycling");
        return ResponseEntity.ok(ApiResponse.success("Batch logged successfully", res));
    }

    @GetMapping("/epr-certificates")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getEPRCertificates() {
        List<Map<String, Object>> certs = new ArrayList<>();
        Map<String, Object> c1 = new HashMap<>();
        c1.put("certificateNo", "EPR-CERT-2026-8891");
        c1.put("producerName", "GreenTech Packagers Ltd.");
        c1.put("plasticCreditKg", 1250.0);
        c1.put("issuedDate", "2026-09-20");
        c1.put("verificationStatus", "VERIFIED_CPCB");
        certs.add(c1);

        Map<String, Object> c2 = new HashMap<>();
        c2.put("certificateNo", "EPR-CERT-2026-9042");
        c2.put("producerName", "Auronyx Eco Corp");
        c2.put("plasticCreditKg", 3400.0);
        c2.put("issuedDate", "2026-09-27");
        c2.put("verificationStatus", "VERIFIED_CPCB");
        certs.add(c2);

        return ResponseEntity.ok(ApiResponse.success(certs));
    }
}
