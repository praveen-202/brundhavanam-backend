package com.brundhavanam.product.service.impl;

import com.brundhavanam.common.exception.ResourceNotFoundException;
import com.brundhavanam.product.entity.Product;
import com.brundhavanam.product.entity.ProductImage;
import com.brundhavanam.product.repository.ProductImageRepository;
import com.brundhavanam.product.repository.ProductRepository;
import com.brundhavanam.product.service.ImageUploadService;
import com.brundhavanam.product.service.ProductImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ImageUploadService imageUploadService;

    public ProductImageServiceImpl(ProductRepository productRepository,
                                   ProductImageRepository productImageRepository,
                                   ImageUploadService imageUploadService) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.imageUploadService = imageUploadService;
    }

    @Override
    public List<String> uploadProductImages(Long productId, List<MultipartFile> images) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        List<String> urls = new ArrayList<>();

        int sortOrder = 1;
        for (MultipartFile file : images) {
            Map<String, String> upload = imageUploadService.uploadImage(file, "products");

            ProductImage productImage = ProductImage.builder()
                    .product(product)
                    .imageUrl(upload.get("url"))
                    .publicId(upload.get("publicId"))
                    .sortOrder(sortOrder++)
                    .build();

            productImageRepository.save(productImage);
            urls.add(productImage.getImageUrl());
        }

        return urls;
    }
}
