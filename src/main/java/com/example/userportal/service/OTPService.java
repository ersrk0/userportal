package com.example.userportal.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class OTPService {
    private final Map<String, String> otpStore = new HashMap<>();

    public String generateOTP(String identifier) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        otpStore.put(identifier, otp);
        return otp;
    }

    public boolean verifyOTP(String identifier, String otp) {
        return otp.equals(otpStore.get(identifier));
    }

    public void clearOTP(String identifier) {
        otpStore.remove(identifier);
    }
}