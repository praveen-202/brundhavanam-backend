package com.brundhavanam.user.service;

public interface OtpService {

    void sendOtp(String mobile);

    boolean verifyOtp(String mobile, String otp);
}
//new