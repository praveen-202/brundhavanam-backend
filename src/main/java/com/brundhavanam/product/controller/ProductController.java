package com.brundhavanam.product.controller;

import com.brundhavanam.common.response.ApiResponse;
import com.brundhavanam.product.dto.ProductResponse;
import com.brundhavanam.product.service.ProductService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ProductController
 *
 * User-facing APIs for viewing products.
 * Base URL: /api/v1/products
 *
 * Key Points:
 * - Exposes product listing and product details APIs for users.
 * - Returns only ACTIVE products to users.
 * - Admin CRUD operations are handled in AdminProductController.
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    // Service layer which contains business logic for product operations
    private final ProductService productService;

    /**
     * Get all active products (User).
     *
     * Endpoint:
     * GET /api/v1/products
     *
     * Response:
     * - List of active products
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        return ResponseEntity.ok(ApiResponse.success(productService.getAllForUser()));
    }

    /**
     * Get product details by product ID (User).
     *
     * Endpoint:
     * GET /api/v1/products/{id}
     *
     * Path Variable:
     * - id : product ID
     *
     * Response:
     * - Product details if product exists and is active
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getByIdForUser(id)));
    }

    /**
     * Get all active products by category (User).
     *
     * Endpoint:
     * GET /api/v1/products/category/{category}
     *
     * Path Variable:
     * - category : product category name (example: Milk, Fruits, etc.)
     *
     * Response:
     * - List of active products matching the category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(ApiResponse.success(productService.getByCategory(category)));
    }
    
    //------------Search API------------------
    /**
     * Search products (User) - searches by name, description, category.
     *
     * Example:
     * GET /api/v1/products/search?query=ghee&page=0&size=10
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> searchProducts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(productService.searchForUser(query, page, size)));
    }
    
    
}
