package com.brundhavanam.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.brundhavanam.common.enums.PaymentMethod;
import com.brundhavanam.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    
    //POST "http://localhost:8080/api/v1/payments/success/101?method=UPI"
    
    @PostMapping("/success/{orderId}")
    public ResponseEntity<Void> success(
            @PathVariable Long orderId,
            @RequestParam PaymentMethod method
    ) {
        paymentService.simulateSuccess(orderId, method);
        return ResponseEntity.ok().build();
    }
    
  //QUICK FLOW====
    //
    //Payment success
    //→ fetch Order
    //→ save Payment
    //→ confirm Order
    //→ deduct stock
}

