package com.avelrune.property.dto;

import com.avelrune.property.enums.ListingType;
import com.avelrune.property.enums.PropertyType;

import java.math.BigDecimal;
import java.time.Instant;

public class PropertySummaryResponse {

    private Long id;
    private String title;
    private String slug;
    private BigDecimal price;
    private ListingType listingType;
    private PropertyType propertyType;
    private String city;
    private String district;
    private String neighborhood;
    private int grossArea;
    private String roomCount;
    private boolean featured;
    private boolean active;
    private String coverImage;
    private Instant createdAt;
    private Long ownerUserId;

    public PropertySummaryResponse(
            Long id,
            String title,
            String slug,
            BigDecimal price,
            ListingType listingType,
            PropertyType propertyType,
            String city,
            String district,
            String neighborhood,
            int grossArea,
            String roomCount,
            boolean featured,
            boolean active,
            String coverImage,
            Instant createdAt,
            Long ownerUserId
    ) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.price = price;
        this.listingType = listingType;
        this.propertyType = propertyType;
        this.city = city;
        this.district = district;
        this.neighborhood = neighborhood;
        this.grossArea = grossArea;
        this.roomCount = roomCount;
        this.featured = featured;
        this.active = active;
        this.coverImage = coverImage;
        this.createdAt = createdAt;
        this.ownerUserId = ownerUserId;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public ListingType getListingType() {
        return listingType;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public int getGrossArea() {
        return grossArea;
    }

    public String getRoomCount() {
        return roomCount;
    }

    public boolean isFeatured() {
        return featured;
    }

    public boolean isActive() {
        return active;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }
}
