package com.brundhavanam.user.service.impl;

import com.brundhavanam.common.enums.Role;
import com.brundhavanam.common.exception.ResourceNotFoundException;
import com.brundhavanam.user.dto.*;
import com.brundhavanam.user.entity.User;
import com.brundhavanam.user.repository.UserRepository;
import com.brundhavanam.user.service.OtpService;
import com.brundhavanam.user.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
//new
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OtpService otpService;

    @Override
    public UserResponse createUser(UserCreateRequest request) {

        if (userRepository.existsByMobile(request.mobile()))
            throw new IllegalArgumentException("Mobile already registered");

        User user = User.builder()
                .fullName(request.fullName())
                .mobile(request.mobile())
                .email(request.email())
                .role(Role.USER)
                .active(true)
                .build();

        return mapToResponse(userRepository.save(user));
    }

    @Override
    public void sendOtp(OtpRequest request) {

        otpService.sendOtp(request.mobile());
    }

    @Override
    public UserResponse verifyOtpAndLogin(OtpVerifyRequest request) {

        boolean valid = otpService.verifyOtp(request.mobile(), request.otp());

        if (!valid)
            throw new IllegalArgumentException("Invalid or expired OTP");

        User user = userRepository.findByMobile(request.mobile())
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .fullName("Guest User")
                                .mobile(request.mobile())
                                .role(Role.USER)
                                .active(true)
                                .build()
                ));

        return mapToResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
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
