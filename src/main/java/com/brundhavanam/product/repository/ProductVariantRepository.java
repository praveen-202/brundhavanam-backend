package com.brundhavanam.product.repository;

import com.brundhavanam.product.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    List<ProductVariant> findByProductId(Long productId);

    List<ProductVariant> findByProductIdAndActiveTrue(Long productId);

    Optional<ProductVariant> findByIdAndActiveTrue(Long id);

    boolean existsByProductIdAndLabelIgnoreCase(Long productId, String label);
    
    long countByProductIdAndActiveTrue(Long productId);

}
