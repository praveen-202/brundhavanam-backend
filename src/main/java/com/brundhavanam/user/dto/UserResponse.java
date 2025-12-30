package com.brundhavanam.user.dto;

import com.brundhavanam.common.enums.Role;

public record UserResponse(
        Long id,
        String fullName,
        String mobile,
        String email,
        Role role,
        Boolean active
) { }
