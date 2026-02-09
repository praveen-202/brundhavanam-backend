package com.brundhavanam.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

import com.brundhavanam.common.enums.UnitType;

public record ProductRequest(
        @NotBlank String name,
        String description,

        @DecimalMin(value = "0.0", inclusive = true)
        BigDecimal price,


        String category,
        String imageUrl,

        Boolean active,
        
        UnitType defaultUnit
) {}

