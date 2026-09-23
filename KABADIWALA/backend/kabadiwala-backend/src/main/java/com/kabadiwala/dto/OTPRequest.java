package com.kabadiwala.dto;

import jakarta.validation.constraints.NotBlank;

public class OTPRequest {

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String otp;

    public OTPRequest() {}

    public OTPRequest(String phone, String otp) {
        this.phone = phone;
        this.otp = otp;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
