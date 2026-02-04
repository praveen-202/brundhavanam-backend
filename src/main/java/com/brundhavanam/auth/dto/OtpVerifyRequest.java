package com.brundhavanam.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OtpVerifyRequest(

        @NotBlank
        @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number")
        String mobile,

        @NotBlank
        String otp

) { }