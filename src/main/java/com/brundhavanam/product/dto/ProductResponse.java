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
        BigDecimal price,
        String category,

        // ✅ Primary image used in listing
        String mainImageUrl,

        // ✅ Full gallery for product details
        List<String> imageUrls,

        Integer stock,
        Boolean active
) { }
