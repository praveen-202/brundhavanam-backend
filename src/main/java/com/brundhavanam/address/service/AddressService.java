package com.brundhavanam.address.service;

import com.brundhavanam.address.dto.*;

import java.util.List;

public interface AddressService {

    AddressResponse add(AddressRequest request);

    List<AddressResponse> myAddresses();

    AddressResponse update(Long id, AddressRequest request);

    void delete(Long id);

    void setDefault(Long id);
}
