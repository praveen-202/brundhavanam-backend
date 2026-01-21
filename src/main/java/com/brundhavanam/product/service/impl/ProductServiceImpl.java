package com.brundhavanam.product.service.impl;

import com.brundhavanam.common.exception.ResourceNotFoundException;
import com.brundhavanam.product.dto.ProductRequest;
import com.brundhavanam.product.dto.ProductResponse;
import com.brundhavanam.product.entity.Product;
import com.brundhavanam.product.repository.ProductRepository;
import com.brundhavanam.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.brundhavanam.product.repository.ProductImageRepository;
import com.brundhavanam.product.entity.ProductImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;

	private final ProductImageRepository productImageRepository;


	// ---------------- Admin ----------------

	@Override
	public ProductResponse create(ProductRequest request) {
		Product product = Product.builder()
				.name(request.name())
				.description(request.description())
				.price(request.price())
				.category(request.category())
				.imageUrl(request.imageUrl())
				.stock(request.stock() == null ? 0 : request.stock())
				.active(request.active() == null ? true : request.active())
				.build();

		return mapToResponse(productRepository.save(product));
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
		product.setStock(request.stock() == null ? product.getStock() : request.stock());

		if (request.active() != null) {
			product.setActive(request.active());
		}

		return mapToResponse(productRepository.save(product));
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
		return productRepository.findAll().stream()
				.map(this::mapToResponse)
				.toList();
	}

	// ---------------- User ----------------

	@Override
	public List<ProductResponse> getAllForUser() {
		return productRepository.findByActiveTrue().stream()
				.map(this::mapToResponse)
				.toList();
	}

	@Override
	public ProductResponse getByIdForUser(Long id) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found"));

		if (!Boolean.TRUE.equals(product.getActive())) {
			throw new ResourceNotFoundException("Product not available");
		}

		return mapToResponse(product);
	}

	@Override
	public List<ProductResponse> getByCategory(String category) {
		return productRepository.findByActiveTrueAndCategoryIgnoreCase(category).stream()
				.map(this::mapToResponse)
				.toList();
	}

	private ProductResponse mapToResponse(Product product) {

		// ✅ Fetch all product image URLs from DB
		List<String> imageUrls = productImageRepository
				.findByProductIdOrderBySortOrderAsc(product.getId())
				.stream()
				.map(ProductImage::getImageUrl)
				.toList();

		// ✅ Main image will be first image (for product listing UI)
		String mainImageUrl = imageUrls.isEmpty() ? null : imageUrls.get(0);

		return new ProductResponse(
				product.getId(),
				product.getName(),
				product.getDescription(),
				product.getPrice(),
				product.getCategory(),
				mainImageUrl,    // ✅ used for listing
				imageUrls,       // ✅ gallery for detail view
				product.getStock(),
				product.getActive()
				);
	}

	//-------------search API -------------
	@Override
	public Page<ProductResponse> searchForUser(String query, int page, int size) {

	    Pageable pageable = PageRequest.of(page, size);

	    return productRepository
	            .findByActiveTrueAndNameContainingIgnoreCaseOrActiveTrueAndDescriptionContainingIgnoreCaseOrActiveTrueAndCategoryContainingIgnoreCase(
	                    query, query, query, pageable
	            )
	            .map(this::mapToResponse);
	}

	@Override
	public Page<ProductResponse> searchForAdmin(String query, int page, int size) {

	    Pageable pageable = PageRequest.of(page, size);

	    return productRepository
	            .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCategoryContainingIgnoreCase(
	                    query, query, query, pageable
	            )
	            .map(this::mapToResponse);
	}

}
