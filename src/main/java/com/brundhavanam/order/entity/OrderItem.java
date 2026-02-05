package com.brundhavanam.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/*
 Permanent snapshot of each purchased product.
 Never changes after order creation.
*/

@Entity
@Table(name = "order_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    private Long productVariantId;

    private String productName;
    private String variantLabel;

    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal itemTotal;
}
