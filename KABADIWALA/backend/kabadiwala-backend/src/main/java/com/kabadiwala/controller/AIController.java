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
}
