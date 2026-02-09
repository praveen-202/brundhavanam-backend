package com.brundhavanam.auth.controller;

import com.brundhavanam.auth.dto.*;
import com.brundhavanam.auth.service.AuthService;
import com.brundhavanam.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/otp/send")
    public ApiResponse<String> sendOtp(@Valid @RequestBody LoginRequest request) {
        authService.sendOtp(request.mobile());
        return ApiResponse.success("OTP sent successfully");
    }

    @PostMapping("/otp/verify")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody OtpVerifyRequest request) {
        return ApiResponse.success(authService.loginWithOtp(request));
    }
}
