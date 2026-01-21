package com.brundhavanam.product.controller;

import com.brundhavanam.common.response.ApiResponse;
import com.brundhavanam.product.dto.ProductRequest;
import com.brundhavanam.product.dto.ProductResponse;
import com.brundhavanam.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AdminProductController
 *
 * Admin-facing APIs for managing products (CRUD operations).
 * Base URL: /api/v1/admin/products
 *
 * Note:
 * These endpoints should be protected later using JWT + Role-based authorization (ADMIN).
 */
@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    // Service layer handles business logic and DB operations
    private final ProductService productService;

    /**
     * Create a new product (Admin).-----
     * Endpoint: POST /api/v1/admin/products
     * 
     * 
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @Valid @RequestBody ProductRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(productService.create(request)));
    }

    /**
     * Update an existing product by ID (Admin).
     * Endpoint: PUT /api/v1/admin/products/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(productService.update(id, request)));
    }

    /**
     * Delete a product by ID (Admin).
     * Endpoint: DELETE /api/v1/admin/products/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully"));
    }

    /**
     * Fetch all products for admin view (includes active/inactive).
     * Endpoint: GET /api/v1/admin/products
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllForAdmin() {
        return ResponseEntity.ok(ApiResponse.success(productService.getAllForAdmin()));
    }
    
    //------------------search API-------------------
    /**
     * Search products (Admin) - includes active and inactive products.
     *
     * Example:
     * GET /api/v1/admin/products/search?query=ghee&page=0&size=10
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> searchProductsForAdmin(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(productService.searchForAdmin(query, page, size)));
    }

}
