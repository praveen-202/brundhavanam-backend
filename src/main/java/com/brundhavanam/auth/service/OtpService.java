package com.brundhavanam.auth.service;

public interface OtpService {

    void sendOtp(String mobile);

    boolean verifyOtp(String mobile, String otp);
}
//added
