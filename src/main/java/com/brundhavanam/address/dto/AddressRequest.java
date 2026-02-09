package com.brundhavanam.address.dto;

import com.brundhavanam.common.enums.AddressLabel;

import jakarta.validation.constraints.NotBlank;

/*
 This DTO is used for:
 - Adding address
 - Updating address

 Frontend can send GPS + manual text both.
*/

public record AddressRequest(
//		{
//			  "label": "HOME", //SHOP/WORK/OTHER
//			  "fullName": "Praveen Kumar",
//			  "mobile": "9876543210",
//			  "street": "MG Road",
//			  "area": "Near Metro",
//			  "city": "Bangalore",
//			  "state": "Karnataka",
//			  "pincode": "560001",
//			  "country": "India",
//			  "latitude": 12.9716,
//			  "longitude": 77.5946,
//			  "isDefault": true
//			}


		AddressLabel label,

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
