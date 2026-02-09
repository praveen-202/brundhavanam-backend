//package com.brundhavanam.user.service;
//
//import com.brundhavanam.user.dto.*;
//import com.brundhavanam.auth.dto.AuthResponse;
//
//import java.util.List;
//
//public interface UserService {
//
//    UserResponse createUser(UserCreateRequest request);
//
//    List<UserResponse> getAllUsers();
//
//    UserResponse getUserById(Long id);
//
//    void deleteUser(Long id);
//
//    void sendOtp(OtpRequest request);
//
//    AuthResponse verifyOtpAndLogin(OtpVerifyRequest request);}
////new

package com.brundhavanam.user.service;

//import com.brundhavanam.auth.dto.AuthResponse;
import com.brundhavanam.user.dto.*;

import java.util.List;

/**
 * UserService defines business operations for the User module.
 *
 * Responsibilities:
 * - User registration & CRUD operations
 * - OTP send and OTP verification
 * - JWT token issuance after successful OTP verification (login)
 *
 * This service is called by {@link com.brundhavanam.user.controller.UserController}.
 */
public interface UserService {

    /**
     * Creates a new user in the system.
     *
     * Business rules (typical):
     * - Mobile number must be unique
     * - Role defaults to USER
     * - User is set as active by default
     *
     * @param request user creation data (fullName, mobile, email)
     * @return created user data as {@link UserResponse}
     * @throws com.brundhavanam.common.exception.BadRequestException if validation fails or mobile already exists
     */
    UserResponse createUser(UserCreateRequest request);

    /**
     * Fetches all users.
     *
     * Generally used in admin use-cases or dashboards.
     *
     * @return list of users
     */
    List<UserResponse> getAllUsers();

    /**
     * Fetches a single user by ID.
     *
     * @param id user id
     * @return user data as {@link UserResponse}
     * @throws com.brundhavanam.common.exception.ResourceNotFoundException if user does not exist
     */
    UserResponse getUserById(Long id);

    /**
     * Deletes a user by ID.
     *
     * @param id user id
     * @throws com.brundhavanam.common.exception.ResourceNotFoundException if user does not exist
     */
    void deleteUser(Long id);

//    /**
//     * Sends OTP to the given mobile number.
//     *
//     * Notes:
//     * - In development phase OTP may be printed in console
//     * - In production it should integrate with SMS/Email provider
//     *
//     * @param request contains mobile number
//     * @throws com.brundhavanam.common.exception.BadRequestException if request is invalid
//     */
//    void sendOtp(OtpRequest request);
//
//    /**
//     * Verifies OTP and logs in the user.
//     *
//     * Flow:
//     * 1) Validate OTP (expired/invalid OTP should fail)
//     * 2) Fetch user by mobile (or create guest user depending on business logic)
//     * 3) Generate JWT token
//     * 4) Return token + user details in {@link AuthResponse}
//     *
//     * @param request OTP verification request (mobile + otp)
//     * @return {@link AuthResponse} containing JWT token and user details
//     * @throws com.brundhavanam.common.exception.BadRequestException if OTP is invalid/expired or user is invalid
//     */
//    AuthResponse verifyOtpAndLogin(OtpVerifyRequest request);

}
