package com.avelrune.property.dto;

import com.avelrune.property.enums.ListingType;
import com.avelrune.property.enums.PropertyType;

public record PropertySearchRequest(
        ListingType listingType,
        String city,
        String district,
        PropertyType propertyType,
        Long minPrice,
        Long maxPrice,
        String rooms,
        Integer minArea,
        Integer maxArea,
        Integer buildingAge,
        Boolean featured,
        String sort,
        Integer page,
        Integer size
) {
}
