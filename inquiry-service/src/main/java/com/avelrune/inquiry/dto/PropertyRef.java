package com.avelrune.inquiry.dto;

public class PropertyRef {

    private Long id;
    private String slug;
    private Long assignedUserId;
    private Long ownerUserId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Long getAssignedUserId() {
        return assignedUserId != null ? assignedUserId : ownerUserId;
    }

    public void setAssignedUserId(Long assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public Long getOwnerUserId() {
        return ownerUserId != null ? ownerUserId : assignedUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }
}
