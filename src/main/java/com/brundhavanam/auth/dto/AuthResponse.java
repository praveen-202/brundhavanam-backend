package com.brundhavanam.auth.dto;

import com.brundhavanam.user.dto.UserResponse;

public record AuthResponse(
        String token,
        UserResponse user
) {}
