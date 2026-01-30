package com.brundhavanam.common.enums;

public enum OrderStatus {

    CREATED,        // order placed, waiting payment
    PAID,           // online payment success
    COD_CONFIRMED,  // cash on delivery accepted
    CONFIRMED,      // stock deducted
    SHIPPED,
    DELIVERED,
    CANCELLED
}
