package com.kabadiwala.controller;

import com.kabadiwala.dto.ApiResponse;
import com.kabadiwala.dto.QRResponseDto;
import com.kabadiwala.dto.TraceabilityDto;
import com.kabadiwala.service.QRService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qr")
public class QRController {

    private final QRService qrService;

    public QRController(QRService qrService) {
        this.qrService = qrService;
    }

    @PostMapping("/generate/{transactionId}")
    public ResponseEntity<ApiResponse<QRResponseDto>> generateQR(@PathVariable Long transactionId) {
        QRResponseDto response = qrService.getOrCreateQRForTransaction(transactionId);
        return ResponseEntity.ok(ApiResponse.success("QR code generated successfully", response));
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<ApiResponse<QRResponseDto>> getQRByTransaction(@PathVariable Long transactionId) {
        QRResponseDto response = qrService.getOrCreateQRForTransaction(transactionId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<TraceabilityDto>> getTraceability(@PathVariable String code) {
        TraceabilityDto dto = qrService.getTraceabilityByCode(code);
        return ResponseEntity.ok(ApiResponse.success("Traceability details fetched", dto));
    }

    @GetMapping(value = "/{code}/image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQRImage(@PathVariable String code) {
        byte[] imageBytes = qrService.getQRImageForCode(code);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(imageBytes);
    }
}
