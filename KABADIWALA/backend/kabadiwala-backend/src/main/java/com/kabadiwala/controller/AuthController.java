package com.kabadiwala.controller;

import com.kabadiwala.dto.ApiResponse;
import com.kabadiwala.dto.AuthResponse;
import com.kabadiwala.dto.LoginRequest;
import com.kabadiwala.dto.RegisterRequest;
import com.kabadiwala.service.AuthService;
import com.kabadiwala.service.OTPService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final OTPService otpService;

    public AuthController(AuthService authService, OTPService otpService) {
        this.authService = authService;
        this.otpService = otpService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/otp/send")
    public ResponseEntity<ApiResponse<Map<String, String>>> sendOtp(@RequestBody Map<String, String> body) {
        String target = body.getOrDefault("target", "");
        String otp = otpService.generateOTP(target);
        Map<String, String> res = new HashMap<>();
        res.put("target", target);
        res.put("message", "OTP sent successfully to " + target);
        res.put("otp", otp);
        return ResponseEntity.ok(ApiResponse.success("OTP sent", res));
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyOtp(@RequestBody Map<String, String> body) {
        String target = body.getOrDefault("target", "");
        String otp = body.getOrDefault("otp", "");
        boolean valid = otpService.verifyOTP(target, otp);
        Map<String, Object> res = new HashMap<>();
        res.put("verified", valid);
        res.put("target", target);
        if (!valid) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid or expired OTP"));
        }
        return ResponseEntity.ok(ApiResponse.success("OTP verified successfully", res));
    }
}
