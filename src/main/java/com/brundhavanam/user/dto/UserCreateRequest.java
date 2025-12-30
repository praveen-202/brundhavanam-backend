package com.brundhavanam.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserCreateRequest(
        @NotBlank String fullName,

        @NotBlank
        @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number")
        String mobile,

        String email
) { }
