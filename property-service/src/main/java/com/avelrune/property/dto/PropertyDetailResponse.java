package com.avelrune.property.dto;

import com.avelrune.property.enums.HeatingType;
import com.avelrune.property.enums.ListingType;
import com.avelrune.property.enums.PropertyType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class PropertyDetailResponse {

    private Long id;
    private String title;
    private String slug;
    private String description;
    private BigDecimal price;
    private ListingType listingType;
    private PropertyType propertyType;
    private String city;
    private String district;
    private String neighborhood;
    private int grossArea;
    private int netArea;
    private String roomCount;
    private int buildingAge;
    private int floor;
    private int totalFloors;
    private HeatingType heatingType;
    private boolean furnished;
    private double latitude;
    private double longitude;
    private boolean featured;
    private boolean active;
    private List<String> features;
    private List<ImageResponse> images;
    private AgentResponse agent;
    private Long assignedUserId;
    private Instant createdAt;
    private Instant updatedAt;

    public PropertyDetailResponse(
            Long id,
            String title,
            String slug,
            String description,
            BigDecimal price,
            ListingType listingType,
            PropertyType propertyType,
            String city,
            String district,
            String neighborhood,
            int grossArea,
            int netArea,
            String roomCount,
            int buildingAge,
            int floor,
            int totalFloors,
            HeatingType heatingType,
            boolean furnished,
            double latitude,
            double longitude,
            boolean featured,
            boolean active,
            List<String> features,
            List<ImageResponse> images,
            AgentResponse agent,
            Long assignedUserId,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.price = price;
        this.listingType = listingType;
        this.propertyType = propertyType;
        this.city = city;
        this.district = district;
        this.neighborhood = neighborhood;
        this.grossArea = grossArea;
        this.netArea = netArea;
        this.roomCount = roomCount;
        this.buildingAge = buildingAge;
        this.floor = floor;
        this.totalFloors = totalFloors;
        this.heatingType = heatingType;
        this.furnished = furnished;
        this.latitude = latitude;
        this.longitude = longitude;
        this.featured = featured;
        this.active = active;
        this.features = features;
        this.images = images;
        this.agent = agent;
        this.assignedUserId = assignedUserId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getSlug() { return slug; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public ListingType getListingType() { return listingType; }
    public PropertyType getPropertyType() { return propertyType; }
    public String getCity() { return city; }
    public String getDistrict() { return district; }
    public String getNeighborhood() { return neighborhood; }
    public int getGrossArea() { return grossArea; }
    public int getNetArea() { return netArea; }
    public String getRoomCount() { return roomCount; }
    public int getBuildingAge() { return buildingAge; }
    public int getFloor() { return floor; }
    public int getTotalFloors() { return totalFloors; }
    public HeatingType getHeatingType() { return heatingType; }
    public boolean isFurnished() { return furnished; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public boolean isFeatured() { return featured; }
    public boolean isActive() { return active; }
    public List<String> getFeatures() { return features; }
    public List<ImageResponse> getImages() { return images; }
    public AgentResponse getAgent() { return agent; }
    public Long getAssignedUserId() { return assignedUserId; }
    public Long getOwnerUserId() { return assignedUserId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
