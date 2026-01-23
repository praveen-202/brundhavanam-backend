package com.brundhavanam.product.service;

import com.brundhavanam.product.dto.CreateVariantRequest;
import com.brundhavanam.product.dto.VariantResponse;

import java.util.List;

public interface ProductVariantService {
	
    VariantResponse createVariant(Long productId, CreateVariantRequest request);
    
    List<VariantResponse> getVariantsByProduct(Long productId, boolean onlyActive);
    
    VariantResponse updateVariant(Long variantId, CreateVariantRequest request);
    
    VariantResponse setVariantActiveStatus(Long variantId, boolean active); // ✅ NEW
    
    void deleteVariant(Long variantId);
    
}

