package com.kabadiwala.controller;

import com.kabadiwala.dto.ApiResponse;
import com.kabadiwala.dto.ConfigResponse;
import com.kabadiwala.dto.HealthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api")
public class SystemController {

    @Value("${spring.application.name:kabadiwala-backend}")
    private String appName;

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<HealthResponse>> getHealth() {
        HealthResponse response = new HealthResponse("UP", "CONNECTED");
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/config")
    public ResponseEntity<ApiResponse<ConfigResponse>> getConfig() {
        ConfigResponse config = new ConfigResponse(
                appName,
                "1.0.0",
                Arrays.asList("en", "hi", "mr", "gu", "ta", "te", "kn"),
                Arrays.asList("ROLE_USER", "ROLE_COLLECTOR", "ROLE_RECYCLER", "ROLE_ADMIN"),
                true
        );
        return ResponseEntity.ok(ApiResponse.success(config));
    }

    @GetMapping("/constants")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getConstants() {
        Map<String, Object> map = new HashMap<>();
        map.put("roles", Arrays.asList("ROLE_USER", "ROLE_COLLECTOR", "ROLE_RECYCLER", "ROLE_ADMIN"));
        map.put("wasteCategories", Arrays.asList("PLASTIC", "PAPER", "METAL", "E_WASTE", "GLASS", "COPPER"));
        map.put("pickupStatuses", Arrays.asList("REQUESTED", "ASSIGNED", "IN_TRANSIT", "WEIGHED", "COMPLETED", "CANCELLED"));
        return ResponseEntity.ok(ApiResponse.success(map));
    }
}
