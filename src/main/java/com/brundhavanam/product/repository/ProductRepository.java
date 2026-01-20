package com.brundhavanam.product.repository;

import com.brundhavanam.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // User side: only active products
    List<Product> findByActiveTrue();

    // Optional: filter by category
    List<Product> findByActiveTrueAndCategoryIgnoreCase(String category);
}
