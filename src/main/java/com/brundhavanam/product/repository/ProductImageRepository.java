package com.brundhavanam.product.repository;

import com.brundhavanam.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    /**
     * Fetch all images for a product (used to show product gallery).
     */
    List<ProductImage> findByProductIdOrderBySortOrderAsc(Long productId);

    /**
     * Delete all images when product is deleted (optional cleanup).
     */
    void deleteByProductId(Long productId);
}
