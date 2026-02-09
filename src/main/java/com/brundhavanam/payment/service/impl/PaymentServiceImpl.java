package com.brundhavanam.payment.service.impl;


import org.springframework.stereotype.Service;

import com.brundhavanam.common.enums.PaymentMethod;
import com.brundhavanam.common.enums.PaymentStatus;
import com.brundhavanam.common.exception.ResourceNotFoundException;
import com.brundhavanam.order.entity.Order;
import com.brundhavanam.order.repository.OrderRepository;
import com.brundhavanam.order.service.OrderService;
import com.brundhavanam.payment.entity.Payment;
import com.brundhavanam.payment.repository.PaymentRepository;
import com.brundhavanam.payment.service.PaymentService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final OrderRepository orderRepository;


    @Transactional
    public void simulateSuccess(Long orderId, PaymentMethod method) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        Payment payment = Payment.builder()
                .order(order)   // ✅ this exists in your entity
                .method(method)
                .status(PaymentStatus.SUCCESS)
                .build();

        paymentRepository.save(payment);

        // confirm order + deduct stock
        orderService.confirmOrder(orderId);
    }


}

