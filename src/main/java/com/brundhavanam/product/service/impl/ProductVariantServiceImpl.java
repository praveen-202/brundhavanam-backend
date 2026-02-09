package com.brundhavanam.product.service.impl;


import com.brundhavanam.common.exception.BadRequestException;
import com.brundhavanam.common.exception.ResourceNotFoundException;
import com.brundhavanam.product.dto.CreateVariantRequest;
import com.brundhavanam.product.dto.VariantResponse;
import com.brundhavanam.product.entity.Product;
import com.brundhavanam.product.entity.ProductVariant;
import com.brundhavanam.product.repository.ProductRepository;
import com.brundhavanam.product.repository.ProductVariantRepository;
import com.brundhavanam.product.service.ProductVariantService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository variantRepository;
    private final ProductRepository productRepository;

    @Override
    public VariantResponse createVariant(Long productId, CreateVariantRequest request) {
        validateCreateRequest(request);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        boolean labelExists = variantRepository.existsByProductIdAndLabelIgnoreCase(productId, request.getLabel());
        if (labelExists) {
            throw new BadRequestException("Variant label already exists for this product: " + request.getLabel());
        }

        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .label(request.getLabel().trim())
                .value(request.getValue())
                .unit(request.getUnit())
                .price(request.getPrice())
                .stock(request.getStock())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        ProductVariant saved = variantRepository.save(variant);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VariantResponse> getVariantsByProduct(Long productId, boolean onlyActive) {
        List<ProductVariant> variants = onlyActive
                ? variantRepository.findByProductIdAndActiveTrue(productId)
                : variantRepository.findByProductId(productId);

        return variants.stream().map(this::mapToResponse).toList();
    }

    @Override
    public VariantResponse updateVariant(Long variantId, CreateVariantRequest request) {
        validateCreateRequest(request);

        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found with id: " + variantId));

        // label uniqueness check only if label changed
        if (!variant.getLabel().equalsIgnoreCase(request.getLabel())) {
            boolean labelExists = variantRepository.existsByProductIdAndLabelIgnoreCase(
                    variant.getProduct().getId(), request.getLabel()
            );
            if (labelExists) {
                throw new BadRequestException("Variant label already exists for this product: " + request.getLabel());
            }
        }

        variant.setLabel(request.getLabel().trim());
        variant.setValue(request.getValue());
        variant.setUnit(request.getUnit());
        variant.setPrice(request.getPrice());
        variant.setStock(request.getStock());
        variant.setActive(request.getActive() != null ? request.getActive() : variant.getActive());

        return mapToResponse(variantRepository.save(variant));
    }

    @Override
    public void deleteVariant(Long variantId) {

        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found with id: " + variantId));

        long activeCount = variantRepository
                .countByProductIdAndActiveTrue(variant.getProduct().getId());

        // 🚫 Block deleting last active variant
        if (activeCount <= 1 && Boolean.TRUE.equals(variant.getActive())) {
            throw new BadRequestException("At least one active variant is required for a product");
        }

        variantRepository.delete(variant);
    }


    private void validateCreateRequest(CreateVariantRequest request) {
        if (request.getLabel() == null || request.getLabel().trim().isEmpty()) {
            throw new BadRequestException("Variant label is required");
        }
        if (request.getValue() == null || request.getValue() <= 0) {
            throw new BadRequestException("Variant value must be > 0");
        }
        if (request.getUnit() == null) {
            throw new BadRequestException("Variant unit is required");
        }
        if (request.getPrice() == null || request.getPrice().signum() <= 0) {
            throw new BadRequestException("Variant price must be > 0");
        }
        if (request.getStock() == null || request.getStock() < 0) {
            throw new BadRequestException("Variant stock must be >= 0");
        }
    }
    
    @Override
    public VariantResponse setVariantActiveStatus(Long variantId, boolean active) {

        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found with id: " + variantId));

        if (!active) { // trying to disable

            long activeCount = variantRepository
                    .countByProductIdAndActiveTrue(variant.getProduct().getId());

            if (activeCount <= 1 && Boolean.TRUE.equals(variant.getActive())) {
                throw new BadRequestException("At least one active variant is required for a product");
            }
        }

        variant.setActive(active);

        ProductVariant saved = variantRepository.save(variant);
        return mapToResponse(saved);
    }


    private VariantResponse mapToResponse(ProductVariant v) {
        return VariantResponse.builder()
                .id(v.getId())
                .label(v.getLabel())
                .value(v.getValue())
                .unit(v.getUnit())
                .price(v.getPrice())
                .stock(v.getStock())
                .active(v.getActive())
                .build();
    }
}
