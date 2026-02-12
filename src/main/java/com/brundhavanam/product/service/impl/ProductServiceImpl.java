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
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductImageRepository imageRepository;

    // ================= ADMIN =================

    @Override
    public ProductResponse create(ProductRequest request) {

        if (request.defaultUnit() == null) {
            throw new BadRequestException("Default unit is required");
        }

        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .category(request.category())
                .active(request.active() == null ? true : request.active())
                .build();

        Product saved = productRepository.save(product);

        // BASE variant (mandatory)
        ProductVariant baseVariant = ProductVariant.builder()
                .product(saved)
                .label("BASE")
                .value(1.0)
                .unit(request.defaultUnit())
                .price(BigDecimal.ZERO)
                .stock(0)
                .active(true)
                .build();

        variantRepository.save(baseVariant);

        return mapForAdmin(saved);
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setCategory(request.category());

        if (request.active() != null) {
            product.setActive(request.active());
        }

        return mapForAdmin(productRepository.save(product));
    }

    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }
        imageRepository.deleteByProductId(id);
        productRepository.deleteById(id);
    }

    @Override
    public List<ProductResponse> getAllForAdmin() {
        return productRepository.findAll()
                .stream()
                .map(this::mapForAdmin)
                .toList();
    }

    // ================= USER =================

    @Override
    public List<ProductResponse> getAllForUser() {
        return productRepository.findByActiveTrue()
                .stream()
                .map(this::mapForUser)
                .toList();
    }

    @Override
    public ProductResponse getByIdForUser(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new ResourceNotFoundException("Product not available");
        }

        return mapForUser(product);
    }

    @Override
    public List<ProductResponse> getByCategory(String category) {
        return productRepository.findByActiveTrueAndCategoryIgnoreCase(category)
                .stream()
                .map(this::mapForUser)
                .toList();
    }

    // ================= SEARCH =================

    @Override
    public Page<ProductResponse> searchForUser(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository
                .findByActiveTrueAndNameContainingIgnoreCaseOrActiveTrueAndDescriptionContainingIgnoreCaseOrActiveTrueAndCategoryContainingIgnoreCase(
                        query, query, query, pageable
                )
                .map(this::mapForUser);
    }

    @Override
    public Page<ProductResponse> searchForAdmin(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCategoryContainingIgnoreCase(
                        query, query, query, pageable
                )
                .map(this::mapForAdmin);
    }

    // ================= MAPPERS =================

    private ProductResponse mapForAdmin(Product product) {
        List<VariantResponse> variants = fetchVariants(product.getId(), false);
        return buildResponse(product, variants);
    }

    private ProductResponse mapForUser(Product product) {
        List<VariantResponse> variants = fetchVariants(product.getId(), true);
        return buildResponse(product, variants);
    }

    private ProductResponse buildResponse(Product product, List<VariantResponse> variants) {

        List<String> images = imageRepository
                .findByProductIdOrderBySortOrderAsc(product.getId())
                .stream()
                .map(ProductImage::getImageUrl)
                .toList();

        BigDecimal minPrice = variants.stream()
                .filter(VariantResponse::getActive)
                .map(VariantResponse::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(null);

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                images.isEmpty() ? null : images.get(0),
                images,
                product.getActive(),
                minPrice,
                variants
        );
    }

    private List<VariantResponse> fetchVariants(Long productId, boolean onlyActive) {
        return (onlyActive
                ? variantRepository.findByProductIdAndActiveTrue(productId)
                : variantRepository.findByProductId(productId))
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
