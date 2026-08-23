package com.avelrune.property.service;

import com.avelrune.property.dto.FilterMetaResponse;
import com.avelrune.property.dto.PageResponse;
import com.avelrune.property.dto.PropertyDetailResponse;
import com.avelrune.property.dto.PropertyStatsResponse;
import com.avelrune.property.dto.PropertySummaryResponse;
import com.avelrune.property.dto.PropertyWriteRequest;
import com.avelrune.property.enums.ListingType;
import com.avelrune.property.enums.PropertyType;

import java.util.List;

public interface PropertyService {

    PageResponse<PropertySummaryResponse> search(
            ListingType listingType,
            String city,
            String district,
            PropertyType propertyType,
            Long minPrice,
            Long maxPrice,
            String rooms,
            Integer minArea,
            Integer maxArea,
            Integer maxBuildingAge,
            Boolean featured,
            boolean includeInactive,
            String sort,
            int page,
            int size
    );

    List<PropertySummaryResponse> featured();

    List<PropertySummaryResponse> listAssignedTo(Long userId);

    PropertyDetailResponse getBySlug(String slug);

    PropertyDetailResponse getById(Long id);

    List<PropertySummaryResponse> similar(String slug);

    FilterMetaResponse meta();

    PropertyStatsResponse stats();

    PropertyDetailResponse create(PropertyWriteRequest request, Long actorId, boolean admin);

    PropertyDetailResponse create(PropertyWriteRequest request);

    PropertyDetailResponse update(Long id, PropertyWriteRequest request, Long actorId, boolean admin);

    PropertyDetailResponse update(Long id, PropertyWriteRequest request);

    void delete(Long id, Long actorId, boolean admin);

    void delete(Long id);

    PropertyDetailResponse getOwned(Long id, Long actorId, boolean admin);

    PropertyDetailResponse getActiveById(Long id);

    PropertyDetailResponse toggleActive(Long id);

    PropertyDetailResponse toggleFeatured(Long id);
}
