package com.avelrune.property.dto;

import com.avelrune.property.enums.HeatingType;
import com.avelrune.property.enums.ListingType;
import com.avelrune.property.enums.PropertyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public class PropertyWriteRequest {

    @NotBlank
    @Size(max = 180)
    @Schema(example = "Kadikoy'de Postman ile acilan 2+1")
    private String title;

    @NotBlank
    @Size(max = 6000)
    @Schema(example = "Bu ilan Postman uzerinden yaratildi. Salon sokaga bakiyor ve mutfak ayri duruyor.")
    private String description;

    @NotNull
    @Positive
    @Schema(example = "12500000")
    private BigDecimal price;

    @NotNull
    @Schema(example = "SALE")
    private ListingType listingType;

    @NotNull
    @Schema(example = "APARTMENT")
    private PropertyType propertyType;

    @NotBlank
    @Size(max = 80)
    @Schema(example = "İstanbul")
    private String city;

    @NotBlank
    @Size(max = 80)
    private String district;

    @NotBlank
    @Size(max = 80)
    private String neighborhood;

    @Positive
    private int grossArea;

    @Positive
    private int netArea;

    @NotBlank
    @Size(max = 20)
    private String roomCount;

    @Min(0)
    private int buildingAge;
    private int floor;
    @Min(0)
    private int totalFloors;

    @NotNull
    private HeatingType heatingType;

    private boolean furnished;
    private double latitude;
    private double longitude;
    private boolean featured;
    private boolean active = true;
    private List<String> features;

    private Long agentId;

    private Long assignedUserId;

    @NotEmpty
    @Size(max = 20)
    private List<@NotBlank @Size(max = 260) String> imageUrls;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public ListingType getListingType() { return listingType; }
    public void setListingType(ListingType listingType) { this.listingType = listingType; }
    public PropertyType getPropertyType() { return propertyType; }
    public void setPropertyType(PropertyType propertyType) { this.propertyType = propertyType; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getNeighborhood() { return neighborhood; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }
    public int getGrossArea() { return grossArea; }
    public void setGrossArea(int grossArea) { this.grossArea = grossArea; }
    public int getNetArea() { return netArea; }
    public void setNetArea(int netArea) { this.netArea = netArea; }
    public String getRoomCount() { return roomCount; }
    public void setRoomCount(String roomCount) { this.roomCount = roomCount; }
    public int getBuildingAge() { return buildingAge; }
    public void setBuildingAge(int buildingAge) { this.buildingAge = buildingAge; }
    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }
    public int getTotalFloors() { return totalFloors; }
    public void setTotalFloors(int totalFloors) { this.totalFloors = totalFloors; }
    public HeatingType getHeatingType() { return heatingType; }
    public void setHeatingType(HeatingType heatingType) { this.heatingType = heatingType; }
    public boolean isFurnished() { return furnished; }
    public void setFurnished(boolean furnished) { this.furnished = furnished; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }
    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }
    public Long getAssignedUserId() { return assignedUserId; }
    public void setAssignedUserId(Long assignedUserId) { this.assignedUserId = assignedUserId; }
    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
}
