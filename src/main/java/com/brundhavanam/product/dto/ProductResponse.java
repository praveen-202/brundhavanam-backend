//package com.brundhavanam.product.dto;
//
//import java.math.BigDecimal;
//
//public record ProductResponse(
//        Long id,
//        String name,
//        String description,
//        BigDecimal price,
//        String category,
//        String imageUrl,
//        Integer stock,
//        Boolean active
//) {}

package com.brundhavanam.product.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * ProductResponse DTO
 *
 * Includes:
 * - mainImageUrl for product card/listing UI
 * - imageUrls for product detail gallery
 */

public record ProductResponse(
        Long id,
        String name,
        String description,

        // ✅ keep temporarily (optional) - explained below
        BigDecimal price,

        String category,

        String mainImageUrl,
        List<String> imageUrls,

        // ✅ keep temporarily (optional)
        Integer stock,

        Boolean active,

        // ✅ NEW FIELD (MOST IMPORTANT)
        List<VariantResponse> variants
) { }

