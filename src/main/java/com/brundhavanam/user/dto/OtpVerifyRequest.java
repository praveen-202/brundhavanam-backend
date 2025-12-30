package com.brundhavanam.user.dto;

public record OtpVerifyRequest(
        String mobile,
        String otp
) { }
