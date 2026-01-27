package com.brundhavanam.cart.repository;

import com.brundhavanam.cart.entity.Cart;
import com.brundhavanam.common.enums.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserIdAndStatus(Long userId, CartStatus status);
}
