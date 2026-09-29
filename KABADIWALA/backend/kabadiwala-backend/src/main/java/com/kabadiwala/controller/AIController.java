package com.kabadiwala.controller;

import com.kabadiwala.dto.ApiResponse;
import com.kabadiwala.service.AIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<ApiResponse<Map<String, Object>>> analyzeWaste(@RequestBody Map<String, Object> request) {
        Map<String, Object> analysis = aiService.analyzeWaste(request);
        return ResponseEntity.ok(ApiResponse.success("AI analysis completed successfully", analysis));
    }

    @PostMapping("/classify")
    public ResponseEntity<ApiResponse<Map<String, Object>>> classifyWaste(@RequestBody Map<String, Object> request) {
        Map<String, Object> classification = aiService.classifyWaste(request);
        return ResponseEntity.ok(ApiResponse.success(classification));
    }

    @PostMapping("/predict-price")
    public ResponseEntity<ApiResponse<Map<String, Object>>> predictPrice(@RequestBody Map<String, Object> request) {
        Map<String, Object> price = aiService.predictPrice(request);
        return ResponseEntity.ok(ApiResponse.success(price));
    }
}
