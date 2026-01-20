package com.brundhavanam.product.controller;

import com.brundhavanam.common.response.ApiResponse;
import com.brundhavanam.product.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Admin APIs for uploading product images.
 */
@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class AdminProductImageController {

    private final ProductImageService productImageService;

    /**
     * Upload multiple images for a product.
     * Endpoint: POST /api/v1/admin/products/{id}/images
     *
     * Postman: form-data key = images (multiple files)
     */
    @PostMapping("/{id}/images")
    public ResponseEntity<ApiResponse<List<String>>> uploadImages(
            @PathVariable Long id,
            @RequestParam("images") List<MultipartFile> images
    ) {
        List<String> urls = productImageService.uploadProductImages(id, images);
        return ResponseEntity.ok(ApiResponse.success(urls));
    }
}
