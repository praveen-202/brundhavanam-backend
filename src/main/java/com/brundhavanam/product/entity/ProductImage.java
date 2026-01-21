package com.brundhavanam.product.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_images")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Store Cloudinary secure_url
    @Column(nullable = false, length = 2000)
    private String imageUrl;

    // Store Cloudinary public_id for delete operations
    @Column(nullable = false, length = 500)
    private String publicId;

    private Integer sortOrder;

    @Builder.Default
    private Boolean primaryImage = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
