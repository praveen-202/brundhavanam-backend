package com.brundhavanam.user.service.impl;

//import com.brundhavanam.auth.dto.AuthResponse;
import com.brundhavanam.common.enums.Role;
import com.brundhavanam.common.exception.BadRequestException;
import com.brundhavanam.common.exception.ResourceNotFoundException;
//import com.brundhavanam.config.jwt.JwtUtil;
import com.brundhavanam.user.dto.*;
import com.brundhavanam.user.entity.User;
import com.brundhavanam.user.repository.UserRepository;
//import com.brundhavanam.user.service.OtpService;
import com.brundhavanam.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
//    private final OtpService otpService;
//    private final JwtUtil jwtUtil;   // ✅ NEW

    @Override
    public UserResponse createUser(UserCreateRequest request) {

        if (userRepository.existsByMobile(request.mobile())) {
            throw new BadRequestException("Mobile already registered");
        }

        User user = User.builder()
                .fullName(request.fullName())
                .mobile(request.mobile())
                .email(request.email())
                .role(Role.USER)
                .active(true)
                .build();

        return mapToResponse(userRepository.save(user));
    }

//    @Override
//    public void sendOtp(OtpRequest request) {
//        otpService.sendOtp(request.mobile());
//    }
//
//    /**
//     * OTP verification + JWT generation
//     */
//    @Override
//    public AuthResponse verifyOtpAndLogin(OtpVerifyRequest request) {
//
//        // 1️⃣ Verify OTP (throws exception if invalid/expired)
//        otpService.verifyOtp(request.mobile(), request.otp());
//
//        // 2️⃣ Fetch existing user OR create guest user
//        User user = userRepository.findByMobile(request.mobile())
//                .orElseGet(() -> userRepository.save(
//                        User.builder()
//                                .fullName("Guest User")
//                                .mobile(request.mobile())
//                                .role(Role.USER)
//                                .active(true)
//                                .build()
//                ));
//
//        // 3️⃣ Generate JWT token
//        String token = jwtUtil.generateToken(user.getMobile());
//
//        // 4️⃣ Build response
//        return new AuthResponse(token, mapToResponse(user));
//    }

    @Override
    public List<UserResponse> getAllUsers() {//have to impl. pagination later
        return userRepository.findAll()
                .stream()
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
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    // 🔹 Mapper method
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
