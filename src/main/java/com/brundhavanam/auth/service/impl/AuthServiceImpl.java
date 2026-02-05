package com.brundhavanam.auth.service.impl;

import com.brundhavanam.auth.dto.*;
import com.brundhavanam.auth.service.AuthService;
import com.brundhavanam.auth.service.OtpService;
import com.brundhavanam.common.enums.Role;
import com.brundhavanam.config.jwt.JwtUtil;
import com.brundhavanam.user.dto.UserResponse;
import com.brundhavanam.user.entity.User;
import com.brundhavanam.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final OtpService otpService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void sendOtp(String mobile) {
        otpService.sendOtp(mobile);
    }

    @Override
    public AuthResponse loginWithOtp(OtpVerifyRequest request) {

        otpService.verifyOtp(request.mobile(), request.otp());

        User user = userRepository.findByMobile(request.mobile())
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .fullName("Guest User")
                                .mobile(request.mobile())
                                .role(Role.USER)
                                .active(true)
                                .build()
                ));

        String token = jwtUtil.generateToken(user.getMobile());

        return new AuthResponse(token, mapToResponse(user));
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getMobile(),
                user.getEmail(),
                user.getRole(),
                user.getActive()
        );
    }
}

