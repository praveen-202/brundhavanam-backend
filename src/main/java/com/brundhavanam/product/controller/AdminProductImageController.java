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
    
    // ---------------- UPDATE image ----------------

    @PutMapping("/{productId}/images/{imageId}")
    public ResponseEntity<ApiResponse<String>> updateImage(
            @PathVariable Long productId,
            @PathVariable Long imageId,
            @RequestParam("images") MultipartFile image
    ) {
        String url = productImageService.updateProductImage(productId, imageId, image);
        return ResponseEntity.ok(ApiResponse.success(url));
    }

    // ---------------- DELETE image ----------------

    @DeleteMapping("/{productId}/images/{imageId}")
    public ResponseEntity<ApiResponse<String>> deleteImage(
            @PathVariable Long productId,
            @PathVariable Long imageId
    ) {
        productImageService.deleteProductImage(productId, imageId);
        return ResponseEntity.ok(ApiResponse.success("Image deleted successfully"));
    }
}
