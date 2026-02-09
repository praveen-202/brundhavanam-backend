package com.brundhavanam.product.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageService {
	
    List<String> uploadProductImages(Long productId, List<MultipartFile> images);
    
    String updateProductImage(Long productId, Long imageId, MultipartFile image);

    void deleteProductImage(Long productId, Long imageId);
}
