package com.brundhavanam.cart.service;

import com.brundhavanam.cart.dto.*;

public interface CartService {

    CartResponse getMyCart();

    CartResponse addItem(AddToCartRequest request);

    CartResponse updateItem(Long cartItemId, UpdateCartItemRequest request);

    CartResponse removeItem(Long cartItemId);

    CartResponse clearCart();
}
