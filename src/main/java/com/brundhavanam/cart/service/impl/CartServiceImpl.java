package com.brundhavanam.cart.service.impl;

import com.brundhavanam.cart.dto.*;
import com.brundhavanam.cart.entity.Cart;
import com.brundhavanam.cart.entity.CartItem;
import com.brundhavanam.cart.repository.CartItemRepository;
import com.brundhavanam.cart.repository.CartRepository;
import com.brundhavanam.common.enums.CartStatus;
import com.brundhavanam.common.exception.BadRequestException;
import com.brundhavanam.common.exception.ResourceNotFoundException;
import com.brundhavanam.product.entity.ProductVariant;
import com.brundhavanam.product.repository.ProductVariantRepository;
import com.brundhavanam.user.entity.User;
import com.brundhavanam.user.repository.UserRepository;
import com.brundhavanam.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getMyCart() {
        User user = getLoggedInUser();

        Cart cart = cartRepository.findByUserIdAndStatus(user.getId(), CartStatus.ACTIVE)
                .orElse(null);

        if (cart == null) {
            return new CartResponse(null, 0, BigDecimal.ZERO, List.of());
        }

        return buildCartResponse(cart);
    }

    @Override
    public CartResponse addItem(AddToCartRequest request) {
        User user = getLoggedInUser();

        Cart cart = cartRepository.findByUserIdAndStatus(user.getId(), CartStatus.ACTIVE)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder()
                                .user(user)
                                .status(CartStatus.ACTIVE)
                                .build()
                ));

        ProductVariant variant = productVariantRepository.findByIdAndActiveTrue(request.variantId())
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found or inactive: " + request.variantId()));

        // ✅ stock validation
        validateStock(variant, request.quantity());

        CartItem cartItem = cartItemRepository.findByCartIdAndVariantId(cart.getId(), variant.getId())
                .orElse(null);

        if (cartItem == null) {
            cartItem = CartItem.builder()
                    .cart(cart)
                    .variant(variant)
                    .quantity(request.quantity())
                    .build();
        } else {
            int updatedQty = cartItem.getQuantity() + request.quantity();
            validateStock(variant, updatedQty);
            cartItem.setQuantity(updatedQty);
        }

        cartItemRepository.save(cartItem);

        return buildCartResponse(cart);
    }

    @Override
    public CartResponse updateItem(Long cartItemId, UpdateCartItemRequest request) {
        User user = getLoggedInUser();

        Cart cart = cartRepository.findByUserIdAndStatus(user.getId(), CartStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active cart not found"));

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + cartItemId));

        // ✅ ensure item belongs to user cart
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("This cart item does not belong to your cart");
        }

        ProductVariant variant = item.getVariant();

        validateStock(variant, request.quantity());
        item.setQuantity(request.quantity());

        cartItemRepository.save(item);
        return buildCartResponse(cart);
    }

    @Override
    public CartResponse removeItem(Long cartItemId) {
        User user = getLoggedInUser();

        Cart cart = cartRepository.findByUserIdAndStatus(user.getId(), CartStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active cart not found"));

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + cartItemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("This cart item does not belong to your cart");
        }

        cartItemRepository.delete(item);
        return buildCartResponse(cart);
    }

    @Override
    public CartResponse clearCart() {
        User user = getLoggedInUser();

        Cart cart = cartRepository.findByUserIdAndStatus(user.getId(), CartStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active cart not found"));

        cartItemRepository.deleteByCartId(cart.getId());

        return new CartResponse(cart.getId(), 0, BigDecimal.ZERO, List.of());
    }

    // =========================================================
    // Helpers
    // =========================================================

    private CartResponse buildCartResponse(Cart cart) {
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

        List<CartItemResponse> responseItems = items.stream().map(item -> {
            ProductVariant v = item.getVariant();

            BigDecimal unitPrice = v.getPrice();
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

            return new CartItemResponse(
                    item.getId(),
                    v.getProduct().getId(),
                    v.getProduct().getName(),
                    v.getId(),
                    v.getLabel(),
                    item.getQuantity(),
                    unitPrice,
                    itemTotal
            );
        }).toList();

        int totalItems = items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        BigDecimal grandTotal = responseItems.stream()
                .map(CartItemResponse::itemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(cart.getId(), totalItems, grandTotal, responseItems);
    }

    private void validateStock(ProductVariant variant, int requiredQty) {

        if (variant.getStock() == null) {
            throw new BadRequestException(
                    "Stock not initialized for variant: " + variant.getLabel()
            );
        }

        if (variant.getStock() < requiredQty) {
            throw new BadRequestException(
                    "Only " + variant.getStock() + " available for variant: " + variant.getLabel()
            );
        }
    }


    private User getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new BadRequestException("Unauthorized");
        }

        String mobile = auth.getPrincipal().toString();

        return userRepository.findByMobile(mobile)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with mobile: " + mobile));
    }
}
