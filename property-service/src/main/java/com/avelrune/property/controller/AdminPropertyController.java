package com.avelrune.property.controller;

import com.avelrune.property.dto.PageResponse;
import com.avelrune.property.dto.PropertyDetailResponse;
import com.avelrune.property.dto.PropertySummaryResponse;
import com.avelrune.property.dto.PropertyWriteRequest;
import com.avelrune.property.enums.ListingType;
import com.avelrune.property.enums.PropertyType;
import com.avelrune.property.security.AuthHeaders;
import com.avelrune.property.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/properties")
public class AdminPropertyController {

    private final PropertyService propertyService;

    public AdminPropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<PropertySummaryResponse>> list(
            @RequestHeader(value = "X-Authenticated-Role", required = false) String role,
            @RequestParam(required = false) ListingType listingType,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) PropertyType propertyType,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        AuthHeaders.requireAdmin(role);
        return ResponseEntity.ok(propertyService.search(
                listingType, city, null, propertyType, null, null,
                null, null, null, null, null, true, sort, page, size
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyDetailResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PropertyDetailResponse> create(@Valid @RequestBody PropertyWriteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(propertyService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyDetailResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PropertyWriteRequest request
    ) {
        return ResponseEntity.ok(propertyService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        propertyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<PropertyDetailResponse> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.toggleActive(id));
    }

    @PatchMapping("/{id}/featured")
    public ResponseEntity<PropertyDetailResponse> toggleFeatured(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.toggleFeatured(id));
    }
}
