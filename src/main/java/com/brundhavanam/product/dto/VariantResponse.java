package com.brundhavanam.product.dto;


import lombok.*;

import java.math.BigDecimal;

import com.brundhavanam.common.enums.UnitType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantResponse {

    private Long id;
    private String label;
    private Double value;
    private UnitType unit;
    private BigDecimal price;
    private Integer stock;
    private Boolean active;
}
