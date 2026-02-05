package com.brundhavanam.user.controller;

//import com.brundhavanam.auth.dto.AuthResponse;
import com.brundhavanam.common.response.ApiResponse;
import com.brundhavanam.user.dto.*;
import com.brundhavanam.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UserController exposes REST APIs related to User module operations.
 * This includes:
 * - User CRUD operations
 */
@RestController
@RequestMapping("/api/v1/users") // Base URL for all User APIs
@RequiredArgsConstructor          // Injects UserService via constructor
public class UserController {

    private final UserService userService;

    /**
     * Create a new user (registration API)---------
     * Accepts UserCreateRequest containing name, email, password, etc.
     * Returns created user details (without exposing password)
     * 
     * POST http://localhost:8080/brundhavanam/api/v1/users
     * {
     * 		
     * }
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.createUser(request)));
    }

    /**
     * Fetch list of all users in the system
     * Typically used by admin dashboard
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers()));
    }

    /**
     * Fetch specific user by ID
     * If user not found → throws ResourceNotFoundException
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id)));
    }

    /**
     * Delete user by ID
     * If user not found → throws ResourceNotFoundException
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }

//    // ---------- OTP APIs ----------
//
//    /**
//     * Send OTP to user (mobile/email depending on implementation)
//     * Validates input and triggers OTP generation + sending
//     */
//    @PostMapping("/otp/send")
//    public ResponseEntity<ApiResponse<String>> sendOtp(@Valid @RequestBody OtpRequest request) {
//        userService.sendOtp(request);
//        return ResponseEntity.ok(ApiResponse.success("OTP sent successfully"));
//    }
//
//    /**
//     * Verify OTP and perform login/registration based on business logic
//     * Returns UserResponse on successful OTP validation
//     */
//    // ✅ updated to AuthResponse
//    @PostMapping("/otp/verify")
//    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
//        return ResponseEntity.ok(ApiResponse.success(userService.verifyOtpAndLogin(request)));
//    }
}


/*UserController implemented with RESTful endpoints:
  ------------------------------------------------


Create user

Get all users

Get user by ID

Delete user

OTP send

OTP verify & login

Proper usage of:
---------------

@Valid

@RequestBody

@PathVariable

Clean API versioning (/api/v1/users)
**/