package com.brundhavanam.address.dto;

import com.brundhavanam.common.enums.AddressLabel;

public record AddressResponse(
        Long id,
        AddressLabel label,
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
