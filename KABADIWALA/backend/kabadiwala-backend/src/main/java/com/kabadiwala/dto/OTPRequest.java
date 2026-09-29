package com.kabadiwala.dto;

import com.kabadiwala.entity.OtpPurpose;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OTPRequest {

    @NotBlank(message = "Target email or phone is required")
    private String target;

    @NotNull(message = "OTP purpose is required")
    private OtpPurpose purpose;

    public OTPRequest() {
    }

    public OTPRequest(String target, OtpPurpose purpose) {
        this.target = target;
        this.purpose = purpose;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public OtpPurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(OtpPurpose purpose) {
        this.purpose = purpose;
    }
}
