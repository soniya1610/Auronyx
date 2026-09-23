package com.kabadiwala.service;

import com.kabadiwala.dto.AIAnalysisResponse;
import com.kabadiwala.dto.AIHealthResponse;
import com.kabadiwala.entity.AIPrediction;
import com.kabadiwala.entity.User;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.AIPredictionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class AIService {

    private static final Logger logger = LoggerFactory.getLogger(AIService.class);

    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp"
    );

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".webp"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    @Value("${ai.service.url:http://localhost:8000}")
    private String aiServiceUrl;

    @Value("${ai.mock.enabled:true}")
    private boolean mockEnabled;

    private final AIPredictionRepository predictionRepository;
    private final RestTemplate restTemplate;

    public AIService(AIPredictionRepository predictionRepository, RestTemplate restTemplate) {
        this.predictionRepository = predictionRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public AIAnalysisResponse analyzeImage(MultipartFile file, User user) {
        validateUploadedImage(file);

        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload.jpg";
        AIAnalysisResponse rawPrediction = null;
        String provider = "FASTAPI";

        // Try calling real FastAPI service
        try {
            rawPrediction = callFastApiPredict(file);
        } catch (RestClientException | IOException e) {
            logger.warn("FastAPI prediction service unavailable at {}/ai/predict: {}.", aiServiceUrl, e.getMessage());
            if (mockEnabled) {
                logger.info("Falling back to internal MockAIProvider for development/demo flow.");
                rawPrediction = generateMockPrediction(originalFilename);
                provider = "MOCK_DEVELOPMENT";
            } else {
                throw new IllegalStateException("AI service is currently unavailable. Please try again later.");
            }
        }

        // Persist AI Prediction record
        AIPrediction entity = new AIPrediction(
                user,
                rawPrediction.getCategory(),
                rawPrediction.getItem(),
                rawPrediction.getCondition(),
                rawPrediction.getEstimatedWeight(),
                rawPrediction.getPriceMin(),
                rawPrediction.getPriceMax(),
                rawPrediction.getConfidence(),
                originalFilename
        );

        AIPrediction saved = predictionRepository.save(entity);

        return new AIAnalysisResponse(
                saved.getId(),
                saved.getCategory(),
                saved.getItem(),
                saved.getCondition(),
                saved.getEstimatedWeight(),
                saved.getPriceMin(),
                saved.getPriceMax(),
                saved.getConfidence(),
                saved.getImageUrl(),
                saved.getCreatedAt(),
                provider
        );
    }

    @Transactional(readOnly = true)
    public AIAnalysisResponse getAnalysisById(Long id) {
        AIPrediction saved = predictionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AIPrediction", "id", id));

        return new AIAnalysisResponse(
                saved.getId(),
                saved.getCategory(),
                saved.getItem(),
                saved.getCondition(),
                saved.getEstimatedWeight(),
                saved.getPriceMin(),
                saved.getPriceMax(),
                saved.getConfidence(),
                saved.getImageUrl(),
                saved.getCreatedAt(),
                "DATABASE"
        );
    }

    public AIHealthResponse checkHealth() {
        try {
            String healthUrl = aiServiceUrl + "/ai/health";
            ResponseEntity<Map> response = restTemplate.getForEntity(healthUrl, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return new AIHealthResponse("UP", aiServiceUrl, "FASTAPI_CONNECTED", "FastAPI service is online");
            }
        } catch (Exception e) {
            logger.debug("FastAPI health check failed: {}", e.getMessage());
        }

        if (mockEnabled) {
            return new AIHealthResponse("DEGRADED", aiServiceUrl, "MOCK_DEVELOPMENT",
                    "FastAPI offline. Fallback MockAIProvider is active for development.");
        }

        return new AIHealthResponse("DOWN", aiServiceUrl, "OFFLINE", "AI service is unreachable");
    }

    private void validateUploadedImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file must not be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed limit of 10MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Unsupported file type: " + contentType + ". Allowed: JPEG, PNG, WEBP");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || ALLOWED_EXTENSIONS.stream().noneMatch(ext -> filename.toLowerCase().endsWith(ext))) {
            throw new IllegalArgumentException("Unsupported file extension. Allowed extensions: .jpg, .jpeg, .png, .webp");
        }
    }

    @SuppressWarnings("unchecked")
    private AIAnalysisResponse callFastApiPredict(MultipartFile file) throws IOException {
        String predictUrl = aiServiceUrl + "/ai/predict";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload.jpg";
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(predictUrl, requestEntity, Map.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RestClientException("FastAPI returned non-200 status: " + response.getStatusCode());
        }

        Map<String, Object> map = response.getBody();
        String category = String.valueOf(map.getOrDefault("category", "E-Waste"));
        String item = String.valueOf(map.getOrDefault("item", "Electronic Waste"));
        String condition = String.valueOf(map.getOrDefault("condition", "USED"));
        BigDecimal estimatedWeight = new BigDecimal(String.valueOf(map.getOrDefault("estimatedWeight", "0.50")))
                .setScale(3, RoundingMode.HALF_UP);
        BigDecimal priceMin = new BigDecimal(String.valueOf(map.getOrDefault("priceMin", "50.00")))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal priceMax = new BigDecimal(String.valueOf(map.getOrDefault("priceMax", "100.00")))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal confidence = new BigDecimal(String.valueOf(map.getOrDefault("confidence", "0.90")))
                .setScale(4, RoundingMode.HALF_UP);

        AIAnalysisResponse res = new AIAnalysisResponse();
        res.setCategory(category);
        res.setItem(item);
        res.setCondition(condition);
        res.setEstimatedWeight(estimatedWeight);
        res.setPriceMin(priceMin);
        res.setPriceMax(priceMax);
        res.setConfidence(confidence);
        return res;
    }

    private AIAnalysisResponse generateMockPrediction(String filename) {
        String lower = filename.toLowerCase();
        AIAnalysisResponse res = new AIAnalysisResponse();

        if (lower.contains("paper") || lower.contains("book") || lower.contains("news")) {
            res.setCategory("Paper");
            res.setItem("Newspaper");
            res.setCondition("CLEAN");
            res.setEstimatedWeight(new BigDecimal("2.500"));
            res.setPriceMin(new BigDecimal("25.00"));
            res.setPriceMax(new BigDecimal("35.00"));
            res.setConfidence(new BigDecimal("0.9100"));
        } else if (lower.contains("plastic") || lower.contains("bottle")) {
            res.setCategory("Plastic");
            res.setItem("Plastic Bottles");
            res.setCondition("EMPTY");
            res.setEstimatedWeight(new BigDecimal("1.200"));
            res.setPriceMin(new BigDecimal("15.00"));
            res.setPriceMax(new BigDecimal("22.00"));
            res.setConfidence(new BigDecimal("0.8900"));
        } else if (lower.contains("metal") || lower.contains("copper") || lower.contains("iron")) {
            res.setCategory("Metal");
            res.setItem("Copper Wire");
            res.setCondition("SCRAP");
            res.setEstimatedWeight(new BigDecimal("1.000"));
            res.setPriceMin(new BigDecimal("30.00"));
            res.setPriceMax(new BigDecimal("40.00"));
            res.setConfidence(new BigDecimal("0.9300"));
        } else {
            // Default demo item: Mobile Phone (E-Waste) as required by demo flow
            res.setCategory("E-Waste");
            res.setItem("Mobile Phone");
            res.setCondition("USED");
            res.setEstimatedWeight(new BigDecimal("0.350"));
            res.setPriceMin(new BigDecimal("80.00"));
            res.setPriceMax(new BigDecimal("120.00"));
            res.setConfidence(new BigDecimal("0.9400"));
        }

        res.setCreatedAt(LocalDateTime.now());
        return res;
    }
}
