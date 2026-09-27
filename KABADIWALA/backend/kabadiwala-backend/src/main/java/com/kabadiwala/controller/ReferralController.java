package com.kabadiwala.controller;

import com.kabadiwala.dto.ApiResponse;
import com.kabadiwala.entity.Referral;
import com.kabadiwala.security.SecurityUtils;
import com.kabadiwala.service.ReferralService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ReferralController — Module 3.
 * Referral code generation and referral stats for authenticated users.
 */
@RestController
@RequestMapping("/api/referrals")
public class ReferralController {

    private final ReferralService referralService;

    public ReferralController(ReferralService referralService) {
        this.referralService = referralService;
    }

    /**
     * GET /api/referrals/my-code
     * Returns (and lazily generates) the referral code for the current user.
     */
    @GetMapping("/my-code")
    public ResponseEntity<ApiResponse<Map<String, String>>> getMyReferralCode() {
        com.kabadiwala.entity.User user = SecurityUtils.getCurrentUser();
        String code = referralService.generateReferralCode(user);
        return ResponseEntity.ok(ApiResponse.success("Your referral code",
                Map.of("referralCode", code)));
    }

    /**
     * GET /api/referrals/my-referrals
     * Lists all users who joined using the current user's referral code.
     */
    @GetMapping("/my-referrals")
    public ResponseEntity<ApiResponse<List<Referral>>> getMyReferrals() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success("Your referrals retrieved",
                referralService.getMyReferrals(userId)));
    }

    /**
     * GET /api/referrals/stats
     * Returns referral performance stats for the current user.
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getReferralStats() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success("Referral stats retrieved",
                referralService.getReferralStats(userId)));
    }
}
