package com.kabadiwala.controller;

import com.kabadiwala.dto.AIAnalysisResponse;
import com.kabadiwala.dto.AIHealthResponse;
import com.kabadiwala.dto.ApiResponse;
import com.kabadiwala.entity.User;
import com.kabadiwala.security.SecurityUtils;
import com.kabadiwala.service.AIService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<AIAnalysisResponse>> analyze(
            @RequestParam("file") MultipartFile file) {
        User currentUser = SecurityUtils.getCurrentUser();
        AIAnalysisResponse response = aiService.analyzeImage(file, currentUser);
        return ResponseEntity.ok(ApiResponse.success("AI analysis completed (estimate only - not final price)", response));
    }

    @GetMapping("/analysis/{id}")
    public ResponseEntity<ApiResponse<AIAnalysisResponse>> getAnalysis(@PathVariable Long id) {
        AIAnalysisResponse response = aiService.getAnalysisById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<AIHealthResponse>> health() {
        AIHealthResponse response = aiService.checkHealth();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/classify")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> classifyWaste(
            @RequestBody(required = false) java.util.Map<String, Object> body) {
        String query = body != null && body.containsKey("text") ? body.get("text").toString() : "Plastic Bottle";
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("query", query);
        result.put("classification", "E-Waste / Recyclable");
        result.put("confidence", 92.5);
        result.put("recommendedAction", "Schedule collector pickup for authorized recycling");
        return ResponseEntity.ok(ApiResponse.success("Classification successful", result));
    }

    @PostMapping("/predict-price")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> predictPrice(
            @RequestBody(required = false) java.util.Map<String, Object> body) {
        String item = body != null && body.containsKey("item") ? body.get("item").toString() : "Mobile";
        double weight = 1.0;
        if (body != null && body.containsKey("weight")) {
            try { weight = Double.parseDouble(body.get("weight").toString()); } catch (Exception ignored) {}
        }
        double estimated = Math.round(weight * 50.0 * 100.0) / 100.0;
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("item", item);
        result.put("weightKg", weight);
        result.put("estimatedPrice", estimated);
        result.put("currency", "INR");
        return ResponseEntity.ok(ApiResponse.success("Price predicted", result));
    }
}
