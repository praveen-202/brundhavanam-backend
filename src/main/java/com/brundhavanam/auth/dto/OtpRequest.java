package com.brundhavanam.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


/**
 * Request body for sending OTP.
 * Used in POST /api/v1/users/otp/send.
 */
public record OtpRequest(

        @NotBlank(message = "Mobile number is required")
        @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number")
        String mobile

) { }