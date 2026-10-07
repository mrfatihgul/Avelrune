package com.avelrune.property.service;

import com.avelrune.property.entity.Property;
import com.avelrune.property.dto.PropertySearchRequest;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public final class PropertySpecifications {

    private PropertySpecifications() {
    }

    public static Predicate equalIgnoreCase(CriteriaBuilder cb, Expression<String> path, String value) {
        return cb.equal(cb.lower(path), cb.lower(cb.literal(value.trim())));
    }

    public static Specification<Property> withFilters(PropertySearchRequest request, Boolean active) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();
            if (active != null) {
                predicates = cb.and(predicates, cb.equal(root.get("active"), active));
            }
            if (request.listingType() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("listingType"), request.listingType()));
            }
            if (request.city() != null && !request.city().isBlank()) {
                predicates = cb.and(predicates, equalIgnoreCase(cb, root.get("city"), request.city()));
            }
            if (request.district() != null && !request.district().isBlank()) {
                predicates = cb.and(predicates, equalIgnoreCase(cb, root.get("district"), request.district()));
            }
            if (request.propertyType() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("propertyType"), request.propertyType()));
            }
            if (request.minPrice() != null) {
                predicates = cb.and(predicates, cb.ge(root.get("price"), request.minPrice()));
            }
            if (request.maxPrice() != null) {
                predicates = cb.and(predicates, cb.le(root.get("price"), request.maxPrice()));
            }
            if (request.rooms() != null && !request.rooms().isBlank()) {
                String value = request.rooms().trim();
                if (value.endsWith("+") && value.length() > 1) {
                    String prefix = value.substring(0, value.length() - 1).trim();
                    if (prefix.matches("\\d+")) {
                        int min = Integer.parseInt(prefix);
                        var matches = new ArrayList<String>();
                        for (int n = min; n <= 12; n++) {
                            matches.add(n + "+1");
                            matches.add(n + "+2");
                            matches.add(String.valueOf(n));
                        }
                        predicates = cb.and(predicates, root.get("roomCount").in(matches));
                    }
                } else {
                    predicates = cb.and(predicates, cb.equal(root.get("roomCount"), value));
                }
            }
            if (request.minArea() != null) {
                predicates = cb.and(predicates, cb.ge(root.get("grossArea"), request.minArea()));
            }
            if (request.maxArea() != null) {
                predicates = cb.and(predicates, cb.le(root.get("grossArea"), request.maxArea()));
            }
            if (request.buildingAge() != null) {
                predicates = cb.and(predicates, cb.le(root.get("buildingAge"), request.buildingAge()));
            }
            if (request.featured() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("featured"), request.featured()));
            }
            return predicates;
        };
    }
}
