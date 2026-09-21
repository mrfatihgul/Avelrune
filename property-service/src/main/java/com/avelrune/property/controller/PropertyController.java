package com.avelrune.property.controller;

import com.avelrune.property.dto.FilterMetaResponse;
import com.avelrune.property.dto.PageResponse;
import com.avelrune.property.dto.PropertyDetailResponse;
import com.avelrune.property.dto.PropertySummaryResponse;
import com.avelrune.property.enums.ListingType;
import com.avelrune.property.enums.PropertyType;
import com.avelrune.property.service.PropertyService;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avelrune.property.dto.PropertyWriteRequest;
import com.avelrune.property.security.AuthHeaders;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<PropertySummaryResponse>> search(
            @RequestParam(required = false) ListingType listingType,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) PropertyType propertyType,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) String rooms,
            @RequestParam(required = false) Integer minArea,
            @RequestParam(required = false) Integer maxArea,
            @RequestParam(required = false) Integer buildingAge,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return ResponseEntity.ok(propertyService.search(
                listingType, city, district, propertyType, minPrice, maxPrice,
                rooms, minArea, maxArea, buildingAge, featured, false, sort, page, size
        ));
    }

    @GetMapping("/featured")
    public ResponseEntity<List<PropertySummaryResponse>> featured() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(propertyService.featured());
    }

    @GetMapping("/meta")
    public ResponseEntity<FilterMetaResponse> meta() {
        return ResponseEntity.ok(propertyService.meta());
    }

    @GetMapping("/mine")
    public ResponseEntity<List<PropertySummaryResponse>> mine(
            @RequestHeader(value = "X-Authenticated-User-Id", required = false) String userId
    ) {
        return ResponseEntity.ok(propertyService.listAssignedTo(AuthHeaders.requireUserId(userId)));
    }

    @GetMapping("/owned/{id}")
    public ResponseEntity<PropertyDetailResponse> owned(
            @PathVariable Long id,
            @RequestHeader(value = "X-Authenticated-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Authenticated-Role", required = false) String role
    ) {
        return ResponseEntity.ok(propertyService.getOwned(id, AuthHeaders.requireUserId(userId), AuthHeaders.isAdmin(role)));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<PropertyDetailResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getActiveById(id));
    }

    @PostMapping
    public ResponseEntity<PropertyDetailResponse> create(
            @Valid @RequestBody PropertyWriteRequest request,
            @RequestHeader(value = "X-Authenticated-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Authenticated-Role", required = false) String role
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(propertyService.create(request, AuthHeaders.requireUserId(userId), AuthHeaders.isAdmin(role)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyDetailResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PropertyWriteRequest request,
            @RequestHeader(value = "X-Authenticated-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Authenticated-Role", required = false) String role
    ) {
        return ResponseEntity.ok(propertyService.update(id, request, AuthHeaders.requireUserId(userId), AuthHeaders.isAdmin(role)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader(value = "X-Authenticated-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Authenticated-Role", required = false) String role
    ) {
        propertyService.delete(id, AuthHeaders.requireUserId(userId), AuthHeaders.isAdmin(role));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{slug}")
    public ResponseEntity<PropertyDetailResponse> get(@PathVariable String slug) {
        return ResponseEntity.ok(propertyService.getBySlug(slug));
    }

    @GetMapping("/{slug}/similar")
    public ResponseEntity<List<PropertySummaryResponse>> similar(@PathVariable String slug) {
        return ResponseEntity.ok(propertyService.similar(slug));
    }

}
