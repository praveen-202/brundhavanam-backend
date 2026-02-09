package com.brundhavanam.auth.service.impl;

import java.time.LocalDateTime;


public class OtpData {

    private final String otp;
    private final LocalDateTime expiry;

    public OtpData(String otp, LocalDateTime expiry) {
        this.otp = otp;
        this.expiry = expiry;
    }

    public String getOtp() {
        return otp;
    }

    public LocalDateTime getExpiry() {
        return expiry;
    }
}

