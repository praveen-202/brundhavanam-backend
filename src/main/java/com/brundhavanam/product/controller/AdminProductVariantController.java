package com.brundhavanam.product.controller;

import com.brundhavanam.product.dto.CreateVariantRequest;
import com.brundhavanam.product.dto.VariantResponse;
import com.brundhavanam.product.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin Product Variant Controller
 *
 * Purpose:
 * - Manage product variants from admin panel
 * - Example variants: 500g, 1kg, 5kg etc.
 *
 * Base URL:
 * - /api/v1/admin/products
 *
 * Notes:
 * - Variants are managed using productId (for create/list)
 * - Variant update/delete is done using variantId (unique identifier)
 */
@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class AdminProductVariantController {

    private final ProductVariantService variantService;

    // =========================================================
    // 1) Create Variant (Admin)
    // =========================================================
    // POST /api/v1/admin/products/{productId}/variants
    // Creates a new variant under a specific product.
    @PostMapping("/{productId}/variants")
    public ResponseEntity<VariantResponse> createVariant(
            @PathVariable Long productId,
            @RequestBody CreateVariantRequest request
    ) {
        VariantResponse response = variantService.createVariant(productId, request);
        return ResponseEntity.ok(response);
    }

    // =========================================================
    // 2) Get All Variants of a Product (Admin)
    // =========================================================
    // GET /api/v1/admin/products/{productId}/variants
    // Returns all variants (active + inactive) for admin view.
    @GetMapping("/{productId}/variants")
    public ResponseEntity<List<VariantResponse>> getVariants(
            @PathVariable Long productId
    ) {
        List<VariantResponse> response = variantService.getVariantsByProduct(productId, false);
        return ResponseEntity.ok(response);
    }

    // =========================================================
    // 3) Update Variant (Admin)
    // =========================================================
    // PUT /api/v1/admin/products/variants/{variantId}
    // Updates a variant using its unique variantId.
    @PutMapping("/variants/{variantId}")
    public ResponseEntity<VariantResponse> updateVariant(
            @PathVariable Long variantId,
            @RequestBody CreateVariantRequest request
    ) {
        VariantResponse response = variantService.updateVariant(variantId, request);
        return ResponseEntity.ok(response);
    }

    // =========================================================
    // 4) Delete Variant (Admin)
    // =========================================================
    // DELETE /api/v1/admin/products/variants/{variantId}
    // Permanently deletes a variant by variantId.
    @DeleteMapping("/variants/{variantId}")
    public ResponseEntity<Void> deleteVariant(@PathVariable Long variantId) {
        variantService.deleteVariant(variantId);
        return ResponseEntity.noContent().build();
    }
    
	 // =========================================================
	 // 5) Disable Variant (Admin)
	 // =========================================================
	 // PATCH /api/v1/admin/products/variants/{variantId}/disable
	 // Disables a variant (active = false)
	 @PatchMapping("/variants/{variantId}/disable")
	 public ResponseEntity<VariantResponse> disableVariant(@PathVariable Long variantId) {
	     VariantResponse response = variantService.setVariantActiveStatus(variantId, false);
	     return ResponseEntity.ok(response);
	 }
	
	 // =========================================================
	 // 6) Enable Variant (Admin)	
	 // =========================================================
	 // PATCH /api/v1/admin/products/variants/{variantId}/enable
	 // Enables a variant (active = true)
	 @PatchMapping("/variants/{variantId}/enable")
	 public ResponseEntity<VariantResponse> enableVariant(@PathVariable Long variantId) {
	     VariantResponse response = variantService.setVariantActiveStatus(variantId, true);
	     return ResponseEntity.ok(response);
	 }

}
