package com.brundhavanam.address.controller;

import com.brundhavanam.address.dto.*;
import com.brundhavanam.address.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 REST endpoints for Address module.
 All APIs require JWT authentication.
*/

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    // ➕ Add new address
    @PostMapping
    public ResponseEntity<AddressResponse> add(@Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.add(request));
    }

    // 📄 Get all my addresses
    @GetMapping
    public ResponseEntity<List<AddressResponse>> myAddresses() {
        return ResponseEntity.ok(addressService.myAddresses());
    }

    // ✏ Update address
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request
    ) {
        return ResponseEntity.ok(addressService.update(id, request));
    }

    // 🗑 Delete address
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        addressService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ⭐ Set default address
    @PatchMapping("/{id}/default")
    public ResponseEntity<Void> setDefault(@PathVariable Long id) {
        addressService.setDefault(id);
        return ResponseEntity.ok().build();
    }
}
