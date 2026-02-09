package com.brundhavanam.product.service.impl;

import com.brundhavanam.common.exception.BadRequestException;
import com.brundhavanam.common.exception.ResourceNotFoundException;
import com.brundhavanam.product.dto.ProductRequest;
import com.brundhavanam.product.dto.ProductResponse;
import com.brundhavanam.product.dto.VariantResponse;
import com.brundhavanam.product.entity.Product;
import com.brundhavanam.product.entity.ProductImage;
import com.brundhavanam.product.entity.ProductVariant;
import com.brundhavanam.product.repository.ProductImageRepository;
import com.brundhavanam.product.repository.ProductRepository;
import com.brundhavanam.product.repository.ProductVariantRepository;
import com.brundhavanam.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductVariantRepository productVariantRepository;

    // ---------------- Admin ----------------

    @Override
    public ProductResponse create(ProductRequest request) {

        if (request.defaultUnit() == null) {
            throw new BadRequestException("Default unit is required");
        }

        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .category(request.category())
                .imageUrl(request.imageUrl())
                .active(request.active() == null ? true : request.active())
                .build();

        Product savedProduct = productRepository.save(product);

        // ✅ Auto-create default variant (stock lives ONLY here)
        ProductVariant defaultVariant = ProductVariant.builder()
                .product(savedProduct)
                .label("Default")
                .value(1.0)
                .unit(request.defaultUnit())
                .price(savedProduct.getPrice())
                .stock(0)   // stock starts at variant level only
                .active(true)
                .build();

        productVariantRepository.save(defaultVariant);

        return mapToResponseForAdmin(savedProduct);
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(request.category());
        product.setImageUrl(request.imageUrl());

        if (request.active() != null) {
            product.setActive(request.active());
        }

        return mapToResponseForAdmin(productRepository.save(product));
    }

    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }
        productRepository.deleteById(id);
    }

    @Override
    public List<ProductResponse> getAllForAdmin() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponseForAdmin)
                .toList();
    }

    // ---------------- User ----------------

    @Override
    public List<ProductResponse> getAllForUser() {
        return productRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponseForUser)
                .toList();
    }

    @Override
    public ProductResponse getByIdForUser(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new ResourceNotFoundException("Product not available");
        }

        return mapToResponseForUser(product);
    }

    @Override
    public List<ProductResponse> getByCategory(String category) {
        return productRepository.findByActiveTrueAndCategoryIgnoreCase(category)
                .stream()
                .map(this::mapToResponseForUser)
                .toList();
    }

    // ---------------- Search ----------------

    @Override
    public Page<ProductResponse> searchForUser(String query, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return productRepository
                .findByActiveTrueAndNameContainingIgnoreCaseOrActiveTrueAndDescriptionContainingIgnoreCaseOrActiveTrueAndCategoryContainingIgnoreCase(
                        query, query, query, pageable
                )
                .map(this::mapToResponseForUser);
    }

    @Override
    public Page<ProductResponse> searchForAdmin(String query, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return productRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCategoryContainingIgnoreCase(
                        query, query, query, pageable
                )
                .map(this::mapToResponseForAdmin);
    }

    // ================= Mapping Layer =================

    private ProductResponse mapToResponseForAdmin(Product product) {
        return buildProductResponse(product, fetchVariantsForAdmin(product.getId()));
    }

    private ProductResponse mapToResponseForUser(Product product) {
        return buildProductResponse(product, fetchVariantsForUser(product.getId()));
    }

    private ProductResponse buildProductResponse(Product product, List<VariantResponse> variants) {

        List<String> imageUrls = productImageRepository
                .findByProductIdOrderBySortOrderAsc(product.getId())
                .stream()
                .map(ProductImage::getImageUrl)
                .toList();

        String mainImageUrl = imageUrls.isEmpty() ? null : imageUrls.get(0);

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                mainImageUrl,
                imageUrls,
                product.getActive(),
                variants
        );
    }

    private List<VariantResponse> fetchVariantsForAdmin(Long productId) {
        return productVariantRepository.findByProductId(productId)
                .stream()
                .map(this::mapVariant)
                .toList();
    }

    private List<VariantResponse> fetchVariantsForUser(Long productId) {
        return productVariantRepository.findByProductIdAndActiveTrue(productId)
                .stream()
                .map(this::mapVariant)
                .toList();
    }

    private VariantResponse mapVariant(ProductVariant v) {
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
