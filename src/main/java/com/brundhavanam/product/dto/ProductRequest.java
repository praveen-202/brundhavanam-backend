package com.brundhavanam.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank String name,
        String description,

        @NotNull @Min(1)
        BigDecimal price,

        String category,
        String imageUrl,

        @Min(0)
        Integer stock,

        Boolean active
) {}
