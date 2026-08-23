package com.avelrune.property.service;

import com.avelrune.property.entity.Property;
import com.avelrune.property.enums.ListingType;
import com.avelrune.property.enums.PropertyType;
import org.springframework.data.jpa.domain.Specification;

public final class PropertySpecifications {

    private PropertySpecifications() {
    }

    public static Specification<Property> withFilters(
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
            Boolean active
    ) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();
            if (active != null) {
                predicates = cb.and(predicates, cb.equal(root.get("active"), active));
            }
            if (listingType != null) {
                predicates = cb.and(predicates, cb.equal(root.get("listingType"), listingType));
            }
            if (city != null && !city.isBlank()) {
                predicates = cb.and(predicates, cb.equal(cb.lower(root.get("city")), city.toLowerCase()));
            }
            if (district != null && !district.isBlank()) {
                predicates = cb.and(predicates, cb.equal(cb.lower(root.get("district")), district.toLowerCase()));
            }
            if (propertyType != null) {
                predicates = cb.and(predicates, cb.equal(root.get("propertyType"), propertyType));
            }
            if (minPrice != null) {
                predicates = cb.and(predicates, cb.ge(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates = cb.and(predicates, cb.le(root.get("price"), maxPrice));
            }
            if (rooms != null && !rooms.isBlank()) {
                if (rooms.endsWith("+")) {
                    predicates = cb.and(predicates, cb.equal(root.get("roomCount"), rooms));
                } else {
                    predicates = cb.and(predicates, cb.equal(root.get("roomCount"), rooms));
                }
            }
            if (minArea != null) {
                predicates = cb.and(predicates, cb.ge(root.get("grossArea"), minArea));
            }
            if (maxArea != null) {
                predicates = cb.and(predicates, cb.le(root.get("grossArea"), maxArea));
            }
            if (maxBuildingAge != null) {
                predicates = cb.and(predicates, cb.le(root.get("buildingAge"), maxBuildingAge));
            }
            if (featured != null) {
                predicates = cb.and(predicates, cb.equal(root.get("featured"), featured));
            }
            return predicates;
        };
    }
}
