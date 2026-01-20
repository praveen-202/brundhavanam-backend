package com.brundhavanam.product.service;

import com.brundhavanam.product.dto.ProductRequest;
import com.brundhavanam.product.dto.ProductResponse;

import java.util.List;

public interface ProductService {

    // Admin operations
    ProductResponse create(ProductRequest request);
    ProductResponse update(Long id, ProductRequest request);
    void delete(Long id);
    List<ProductResponse> getAllForAdmin();

    // User operations
    List<ProductResponse> getAllForUser();
    ProductResponse getByIdForUser(Long id);

    // Optional
    List<ProductResponse> getByCategory(String category);
}
