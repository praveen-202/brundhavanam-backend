package com.brundhavanam.address.service.impl;

import com.brundhavanam.address.dto.*;
import com.brundhavanam.address.entity.Address;
import com.brundhavanam.address.repository.AddressRepository;
import com.brundhavanam.address.service.AddressService;
import com.brundhavanam.common.exception.BadRequestException;
import com.brundhavanam.common.exception.ResourceNotFoundException;
import com.brundhavanam.user.entity.User;
import com.brundhavanam.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 Handles business logic:
 - user ownership validation
 - default address handling
 - mapping entity → response
*/

@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public AddressResponse add(AddressRequest request) {

        User user = getLoggedInUser();

        // If new address is default → clear existing default
        if (Boolean.TRUE.equals(request.isDefault())) {
            clearDefault(user.getId());
        }

        Address address = Address.builder()
                .user(user)
                .label(request.label())
                .fullName(request.fullName())
                .mobile(request.mobile())
                .street(request.street())
                .area(request.area())
                .city(request.city())
                .state(request.state())
                .pincode(request.pincode())
                .country(request.country())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .isDefault(Boolean.TRUE.equals(request.isDefault()))
                .build();

        return map(addressRepository.save(address));
    }

    @Override
    public List<AddressResponse> myAddresses() {

        User user = getLoggedInUser();

        return addressRepository.findByUserId(user.getId())
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public AddressResponse update(Long id, AddressRequest request) {

        User user = getLoggedInUser();

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        // Prevent user from editing others address
        if (!address.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Not your address");
        }

        if (Boolean.TRUE.equals(request.isDefault())) {
            clearDefault(user.getId());
            address.setIsDefault(true);
        }

        address.setLabel(request.label());
        address.setFullName(request.fullName());
        address.setMobile(request.mobile());
        address.setStreet(request.street());
        address.setArea(request.area());
        address.setCity(request.city());
        address.setState(request.state());
        address.setPincode(request.pincode());
        address.setCountry(request.country());
        address.setLatitude(request.latitude());
        address.setLongitude(request.longitude());

        return map(addressRepository.save(address));
    }

    @Override
    public void delete(Long id) {

        User user = getLoggedInUser();

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Not your address");
        }

        addressRepository.delete(address);
    }

    @Override
    public void setDefault(Long id) {

        User user = getLoggedInUser();

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        clearDefault(user.getId());

        address.setIsDefault(true);
        addressRepository.save(address);
    }

    // ========== HELPER METHODS ==========

    private void clearDefault(Long userId) {
        addressRepository.clearDefaultForUser(userId);
    }


    private AddressResponse map(Address a) {
        return new AddressResponse(
                a.getId(),
                a.getLabel(),
                a.getFullName(),
                a.getMobile(),
                a.getStreet(),
                a.getArea(),
                a.getCity(),
                a.getState(),
                a.getPincode(),
                a.getCountry(),
                a.getLatitude(),
                a.getLongitude(),
                a.getIsDefault()
        );
    }

    private User getLoggedInUser() {
        String mobile = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();

        return userRepository.findByMobile(mobile)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
