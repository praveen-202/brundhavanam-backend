package com.brundhavanam.address.dto;

public record AddressResponse(
        Long id,
        String label,
        String fullName,
        String mobile,
        String street,
        String area,
        String city,
        String state,
        String pincode,
        String country,
        Double latitude,
        Double longitude,
        Boolean isDefault
) {}
