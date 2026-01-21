package com.brundhavanam.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brundhavanam.auth.dto.AuthResponse;
import com.brundhavanam.common.response.ApiResponse;
import com.brundhavanam.user.dto.OtpVerifyRequest;
import com.brundhavanam.user.service.UserService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/otp/verify")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(
            @Valid @RequestBody OtpVerifyRequest request
    ) {
        return ResponseEntity.ok(
            ApiResponse.success(userService.verifyOtpAndLogin(request))
        );
    }
}

