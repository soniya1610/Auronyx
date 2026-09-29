package com.kabadiwala.controller;

import com.kabadiwala.dto.*;
import com.kabadiwala.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<UserResponse>> getProfile() {
        UserResponse response = userService.getProfile();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@Valid @RequestBody UserUpdateRequest request) {
        UserResponse response = userService.updateProfile(request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }

    @PutMapping("/location")
    public ResponseEntity<ApiResponse<UserResponse>> updateLocation(@Valid @RequestBody LocationUpdateRequest request) {
        UserResponse response = userService.updateLocation(request);
        return ResponseEntity.ok(ApiResponse.success("Location updated successfully", response));
    }

    @PutMapping("/language")
    public ResponseEntity<ApiResponse<UserResponse>> updateLanguage(@Valid @RequestBody LanguageUpdateRequest request) {
        UserResponse response = userService.updateLanguage(request);
        return ResponseEntity.ok(ApiResponse.success("Language preference updated successfully", response));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<UserDashboardResponse>> getDashboard() {
        UserDashboardResponse response = userService.getDashboard();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/impact")
    public ResponseEntity<ApiResponse<UserImpactResponse>> getImpact() {
        UserImpactResponse response = userService.getImpact();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
