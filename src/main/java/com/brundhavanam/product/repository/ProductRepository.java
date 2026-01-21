package com.brundhavanam.product.repository;

import com.brundhavanam.product.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // User side: only active products
    List<Product> findByActiveTrue();

    // Optional: filter by category
    List<Product> findByActiveTrueAndCategoryIgnoreCase(String category);
    
    //-----------------for search API------------------
    
    // ✅ USER Search: Only active products
    Page<Product> findByActiveTrueAndNameContainingIgnoreCaseOrActiveTrueAndDescriptionContainingIgnoreCaseOrActiveTrueAndCategoryContainingIgnoreCase(
            String name, String description, String category, Pageable pageable
    );

    // ✅ ADMIN Search: All products (active/inactive)
    Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCategoryContainingIgnoreCase(
            String name, String description, String category, Pageable pageable
    );
}
