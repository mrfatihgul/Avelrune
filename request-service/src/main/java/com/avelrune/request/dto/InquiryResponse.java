package com.avelrune.request.dto;

import java.time.Instant;

public class InquiryResponse {

    private Long id;
    private String fullName;
    private String phone;
    private String email;
    private String message;
    private String propertySlug;
    private Long propertyId;
    private Long ownerUserId;
    private Long senderUserId;
    private boolean member;
    private boolean read;
    private Instant createdAt;

    public InquiryResponse(
            Long id,
            String fullName,
            String phone,
            String email,
            String message,
            String propertySlug,
            Long propertyId,
            Long ownerUserId,
            Long senderUserId,
            boolean member,
            boolean read,
            Instant createdAt
    ) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.message = message;
        this.propertySlug = propertySlug;
        this.propertyId = propertyId;
        this.ownerUserId = ownerUserId;
        this.senderUserId = senderUserId;
        this.member = member;
        this.read = read;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }

    public String getPropertySlug() {
        return propertySlug;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public Long getSenderUserId() {
        return senderUserId;
    }

    public boolean isMember() {
        return member;
    }

    public boolean isRead() {
        return read;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
