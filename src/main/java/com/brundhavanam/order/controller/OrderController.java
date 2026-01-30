package com.brundhavanam.order.controller;

import com.brundhavanam.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 🧾 Checkout (creates order only)
    @PostMapping("/checkout/{addressId}")
    public ResponseEntity<Long> checkout(@PathVariable Long addressId) {
        return ResponseEntity.ok(orderService.checkout(addressId));
    }

    // 💰 Payment success / COD confirm
    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<Void> confirm(@PathVariable Long orderId) {
        orderService.confirmOrder(orderId);
        return ResponseEntity.ok().build();
    }
}
	