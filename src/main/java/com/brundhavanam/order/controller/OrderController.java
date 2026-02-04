package com.brundhavanam.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.brundhavanam.order.service.OrderService;

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

    
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }

}
	