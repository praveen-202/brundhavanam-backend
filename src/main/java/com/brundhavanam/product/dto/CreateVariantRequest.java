package com.brundhavanam.product.dto;


import lombok.*;

import java.math.BigDecimal;

import com.brundhavanam.common.enums.UnitType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVariantRequest {

    private String label;      // 500g, 1kg
    private Double value;      // 500, 1, 5
    private UnitType unit;     // GRAM/KG/LITER
    private BigDecimal price;  // 75.00
    private Integer stock;     // 25
    private Boolean active;    // true
}
