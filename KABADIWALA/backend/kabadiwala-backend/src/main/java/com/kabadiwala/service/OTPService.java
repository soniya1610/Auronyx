package com.kabadiwala.service;

import com.kabadiwala.entity.OtpPurpose;
import com.kabadiwala.entity.OtpToken;
import com.kabadiwala.exception.BadRequestException;
import com.kabadiwala.repository.OtpRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class OTPService {

    private static final Logger log = LoggerFactory.getLogger(OTPService.class);
    private final SecureRandom secureRandom = new SecureRandom();

    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.otp.expiration-minutes:5}")
    private int expirationMinutes;

    @Value("${app.otp.max-attempts:3}")
    private int maxAttempts;

    public OTPService(OtpRepository otpRepository, PasswordEncoder passwordEncoder) {
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public String generateAndSendOtp(String target, OtpPurpose purpose) {
        if (target == null || target.trim().isEmpty()) {
            throw new BadRequestException("Target email or phone is required to generate OTP");
        }

        // Generate 6-digit numeric OTP
        int code = 100000 + secureRandom.nextInt(900000);
        String rawOtp = String.valueOf(code);

        // Hash OTP before persistence
        String hashedOtp = passwordEncoder.encode(rawOtp);
        Instant expiresAt = Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES);

        OtpToken otpToken = new OtpToken(target.trim(), hashedOtp, purpose, expiresAt);
        otpRepository.save(otpToken);

        // Mock dispatch for development (never expose OTP in production)
        log.info("Mock OTP generated for target [{}] with purpose [{}]. In dev mode only: {}", target, purpose, rawOtp);

        return rawOtp;
    }

    @Transactional
    public boolean verifyOtp(String target, String rawOtp, OtpPurpose purpose) {
        OtpToken otpToken = otpRepository.findTopByTargetAndPurposeOrderByCreatedAtDesc(target.trim(), purpose)
                .orElseThrow(() -> new BadRequestException("No OTP found for " + target + " with purpose " + purpose));

        if (otpToken.isVerified()) {
            throw new BadRequestException("This OTP has already been verified and consumed");
        }

        if (otpToken.isExpired()) {
            throw new BadRequestException("OTP has expired. Please request a new one.");
        }

        if (otpToken.getAttempts() >= maxAttempts) {
            throw new BadRequestException("Maximum OTP verification attempts exceeded. Please request a new OTP.");
        }

        boolean matches = passwordEncoder.matches(rawOtp, otpToken.getOtpCode());
        otpToken.setAttempts(otpToken.getAttempts() + 1);

        if (!matches) {
            otpRepository.save(otpToken);
            throw new BadRequestException("Invalid OTP code. Attempts remaining: " + (maxAttempts - otpToken.getAttempts()));
        }

        otpToken.setVerified(true);
        otpRepository.save(otpToken);
        return true;
    }

    @Transactional
    public void consumeOtp(String target, String rawOtp, OtpPurpose purpose) {
        verifyOtp(target, rawOtp, purpose);
    }
}
