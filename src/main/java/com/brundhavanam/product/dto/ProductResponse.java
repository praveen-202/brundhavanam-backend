package com.brundhavanam.product.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        Long id,
        String name,
        String description,
        String category,

        String mainImageUrl,
        List<String> imageUrls,

        Boolean active,

        BigDecimal minPrice,
        List<VariantResponse> variants
) {}



//package com.brundhavanam.product.dto;
//
//import java.math.BigDecimal;
//import java.util.List;
//
///**
// * ProductResponse DTO
// *
// * Includes:
// * - mainImageUrl for product card/listing UI
// * - imageUrls for product detail gallery
// */
//
//public record ProductResponse(
//        Long id,
//        String name,
//        String description,
//
//        // ✅ keep temporarily (optional) - explained below
//        BigDecimal price,
//
//        String category,
//
//        String mainImageUrl,
//        List<String> imageUrls,
//
//        Boolean active,
//
//        // ✅ NEW FIELD (MOST IMPORTANT)
//        List<VariantResponse> variants
//) { }
//
