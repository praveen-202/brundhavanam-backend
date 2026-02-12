package com.brundhavanam.payment.entity;

import com.brundhavanam.common.enums.PaymentMethod;
import com.brundhavanam.common.enums.PaymentStatus;
import com.brundhavanam.order.entity.Order;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Order order;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;


    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    
    private String transactionId; //for future gateways,(for Razorpay / PhonePe callbacks)

    private LocalDateTime paidAt;
    
    @Column(unique = true, nullable = false)
    private String paymentIdempotencyKey;


}
