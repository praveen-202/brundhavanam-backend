package com.brundhavanam.product.service;

import com.brundhavanam.product.dto.ProductRequest;
import com.brundhavanam.product.dto.ProductResponse;

import java.util.List;

import org.springframework.data.domain.Page;

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
    
    //---------search API--------------
    Page<ProductResponse> searchForUser(String query, int page, int size);

    Page<ProductResponse> searchForAdmin(String query, int page, int size);
}
