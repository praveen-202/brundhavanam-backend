package com.brundhavanam.cart.dto;

import java.math.BigDecimal;

public record CartItemResponse(
        Long cartItemId,
        Long productId,
        String productName,
        Long variantId,
        String variantLabel,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal itemTotal
) {}
