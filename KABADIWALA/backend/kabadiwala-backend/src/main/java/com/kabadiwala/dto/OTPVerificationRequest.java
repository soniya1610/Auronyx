package com.kabadiwala.dto;

import com.kabadiwala.entity.OtpPurpose;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class OTPVerificationRequest {

    @NotBlank(message = "Target email or phone is required")
    private String target;

    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP must be a 6-digit number")
    private String otp;

    @NotNull(message = "OTP purpose is required")
    private OtpPurpose purpose;

    public OTPVerificationRequest() {
    }

    public OTPVerificationRequest(String target, String otp, OtpPurpose purpose) {
        this.target = target;
        this.otp = otp;
        this.purpose = purpose;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public OtpPurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(OtpPurpose purpose) {
        this.purpose = purpose;
    }
}
