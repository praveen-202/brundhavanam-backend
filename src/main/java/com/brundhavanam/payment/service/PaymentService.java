package com.brundhavanam.payment.service;

import com.brundhavanam.common.enums.PaymentMethod;

public interface PaymentService {
    void simulateSuccess(Long orderId, PaymentMethod method);
}
