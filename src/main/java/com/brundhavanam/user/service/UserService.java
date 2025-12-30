package com.brundhavanam.user.service;

import com.brundhavanam.user.dto.*;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserCreateRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    void deleteUser(Long id);

    void sendOtp(OtpRequest request);

    UserResponse verifyOtpAndLogin(OtpVerifyRequest request);
}
//new