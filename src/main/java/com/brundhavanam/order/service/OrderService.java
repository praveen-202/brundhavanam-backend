package com.brundhavanam.order.service;

public interface OrderService {

    Long checkout(Long addressId);

    void confirmOrder(Long orderId); // after payment or COD
}
