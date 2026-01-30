package com.brundhavanam.address.dto;

import jakarta.validation.constraints.NotBlank;

/*
 This DTO is used for:
 - Adding address
 - Updating address

 Frontend can send GPS + manual text both.
*/

public record AddressRequest(

        String label,

        @NotBlank String fullName,
        @NotBlank String mobile,

        @NotBlank String street,
        String area,
        @NotBlank String city,
        @NotBlank String state,
        @NotBlank String pincode,
        String country,

        // GPS values for map
        Double latitude,
        Double longitude,

        Boolean isDefault
) {}
