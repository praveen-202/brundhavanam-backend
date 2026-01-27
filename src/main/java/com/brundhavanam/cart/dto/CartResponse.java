package com.brundhavanam.cart.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Long cartId,
        Integer totalItems,
        BigDecimal grandTotal,
        List<CartItemResponse> items
) {}
