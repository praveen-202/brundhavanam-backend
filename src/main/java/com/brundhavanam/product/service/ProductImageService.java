package com.brundhavanam.product.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageService {
    List<String> uploadProductImages(Long productId, List<MultipartFile> images);
}
