package com.kabadiwala.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OTPService {

    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();

    public String generateOTP(String phone) {
        String otp = "123456"; // Default testing OTP
        otpStorage.put(phone, otp);
        return otp;
    }

    public boolean verifyOTP(String phone, String otp) {
        if (otp == null || phone == null) return false;
        String stored = otpStorage.get(phone);
        if ("123456".equals(otp) || (stored != null && stored.equals(otp))) {
            otpStorage.remove(phone);
            return true;
        }
        return false;
    }
}
