package com.brundhavanam.order.service.impl;

import com.brundhavanam.address.entity.Address;
import com.brundhavanam.address.repository.AddressRepository;
import com.brundhavanam.cart.entity.Cart;
import com.brundhavanam.cart.entity.CartItem;
import com.brundhavanam.cart.repository.CartItemRepository;
import com.brundhavanam.cart.repository.CartRepository;
import com.brundhavanam.common.enums.CartStatus;
import com.brundhavanam.common.enums.OrderStatus;
import com.brundhavanam.common.exception.ResourceNotFoundException;
import com.brundhavanam.order.entity.Order;
import com.brundhavanam.order.repository.OrderRepository;
import com.brundhavanam.order.service.OrderService;

import com.brundhavanam.product.entity.ProductVariant;
import com.brundhavanam.product.repository.ProductVariantRepository;

import com.brundhavanam.user.entity.User;
import com.brundhavanam.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final ProductVariantRepository variantRepository;
    private final UserRepository userRepository;

    // ================= CHECKOUT =================

    @Override
    public Long checkout(Long addressId) {

        User user = getLoggedInUser();

        Cart cart = cartRepository
                .findByUserIdAndStatus(user.getId(), CartStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Cart is empty"));

        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

        if (items.isEmpty()) {
            throw new ResourceNotFoundException("No items in cart");
        }

        BigDecimal totalAmount = items.stream()
                .map(i -> i.getVariant().getPrice()
                        .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        Order order = Order.builder()
                .user(user)
                .totalAmount(totalAmount)
                .status(OrderStatus.CREATED)

                .fullName(address.getFullName())
                .mobile(address.getMobile())
                .street(address.getStreet())
                .area(address.getArea())
                .city(address.getCity())
                .state(address.getState())
                .pincode(address.getPincode())
                .country(address.getCountry())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())

                .build();

        orderRepository.save(order);

        cart.setStatus(CartStatus.CHECKED_OUT);
        cartRepository.save(cart);

        return order.getId();
    }

    // ================= CONFIRM AFTER PAYMENT =================

    @Override
    public void confirmOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        Cart cart = cartRepository
                .findByUserIdAndStatus(order.getUser().getId(), CartStatus.CHECKED_OUT)
                .orElseThrow();

        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

        // ✅ Deduct stock using ProductVariant
        for (CartItem item : items) {
            ProductVariant variant = item.getVariant();
            variant.setStock(variant.getStock() - item.getQuantity());
            variantRepository.save(variant);
        }

        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
    }

    // ================= HELPER =================

    private User getLoggedInUser() {
        String mobile = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();

        return userRepository.findByMobile(mobile)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
