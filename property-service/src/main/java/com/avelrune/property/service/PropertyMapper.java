package com.avelrune.property.service;

import com.avelrune.property.dto.AgentResponse;
import com.avelrune.property.dto.ImageResponse;
import com.avelrune.property.dto.PropertyDetailResponse;
import com.avelrune.property.dto.PropertySummaryResponse;
import com.avelrune.property.entity.Agent;
import com.avelrune.property.entity.Property;
import com.avelrune.property.entity.PropertyImage;

import java.util.Arrays;
import java.util.List;

public final class PropertyMapper {

    private PropertyMapper() {
    }

    public static PropertySummaryResponse toSummary(Property property) {
        String cover = property.getImages().isEmpty() ? null : property.getImages().get(0).getUrl();
        return new PropertySummaryResponse(
                property.getId(),
                property.getTitle(),
                property.getSlug(),
                property.getPrice(),
                property.getListingType(),
                property.getPropertyType(),
                property.getCity(),
                property.getDistrict(),
                property.getNeighborhood(),
                property.getGrossArea(),
                property.getRoomCount(),
                property.isFeatured(),
                property.isActive(),
                cover,
                property.getCreatedAt(),
                property.getAssignedUserId()
        );
    }

    public static PropertyDetailResponse toDetail(Property property) {
        return new PropertyDetailResponse(
                property.getId(),
                property.getTitle(),
                property.getSlug(),
                property.getDescription(),
                property.getPrice(),
                property.getListingType(),
                property.getPropertyType(),
                property.getCity(),
                property.getDistrict(),
                property.getNeighborhood(),
                property.getGrossArea(),
                property.getNetArea(),
                property.getRoomCount(),
                property.getBuildingAge(),
                property.getFloor(),
                property.getTotalFloors(),
                property.getHeatingType(),
                property.isFurnished(),
                property.getLatitude(),
                property.getLongitude(),
                property.isFeatured(),
                property.isActive(),
                parseFeatures(property.getFeatures()),
                property.getImages().stream().map(PropertyMapper::toImage).toList(),
                property.getAgent() == null ? null : toAgent(property.getAgent()),
                property.getAssignedUserId(),
                property.getCreatedAt(),
                property.getUpdatedAt()
        );
    }

    public static AgentResponse toAgent(Agent agent) {
        return new AgentResponse(
                agent.getId(),
                agent.getFullName(),
                agent.getTitle(),
                agent.getBio(),
                agent.getPhone(),
                agent.getEmail(),
                agent.getPhotoUrl(),
                agent.getExpertise()
        );
    }

    private static ImageResponse toImage(PropertyImage image) {
        return new ImageResponse(image.getId(), image.getUrl(), image.getAltText(), image.getSortOrder());
    }

    private static List<String> parseFeatures(String features) {
        if (features == null || features.isBlank()) {
            return List.of();
        }
        return Arrays.stream(features.split("\\|"))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .toList();
    }
}
