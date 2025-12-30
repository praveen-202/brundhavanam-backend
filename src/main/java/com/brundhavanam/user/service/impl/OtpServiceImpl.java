package com.brundhavanam.user.service.impl;

import com.brundhavanam.user.service.OtpService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
//new
@Service
public class OtpServiceImpl implements OtpService {

    private final Map<String, String> otpStore = new HashMap<>();
    private final Map<String, LocalDateTime> expiryStore = new HashMap<>();

    @Override
    public void sendOtp(String mobile) {

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        otpStore.put(mobile, otp);
        expiryStore.put(mobile, LocalDateTime.now().plusMinutes(5));

        // TODO integrate real SMS gateway here
        System.out.println("OTP for " + mobile + " = " + otp);
    }

    @Override
    public boolean verifyOtp(String mobile, String otp) {

        if (!otpStore.containsKey(mobile)) return false;

        if (expiryStore.get(mobile).isBefore(LocalDateTime.now())) return false;

        return otpStore.get(mobile).equals(otp);
    }
}
