package com.avelrune.inquiry.entity;

import com.avelrune.inquiry.enums.InquirySituation;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "inquiries")
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, length = 40)
    private String phone;

    @Column(nullable = false, length = 180)
    private String email;

    @Column(nullable = false, length = 3000)
    private String message;

    @Column(length = 180)
    private String propertySlug;

    private Long propertyId;

    private Long ownerUserId;

    private Long senderUserId;

    @Column(nullable = false)
    private boolean readFlag = false;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false) @Enumerated(EnumType.STRING)
    private InquirySituation status = InquirySituation.UNDERREVIEW;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public InquirySituation getStatus() {
        return status;
    }

    public void setStatus(InquirySituation status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPropertySlug() {
        return propertySlug;
    }

    public void setPropertySlug(String propertySlug) {
        this.propertySlug = propertySlug;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public Long getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(Long senderUserId) {
        this.senderUserId = senderUserId;
    }

    public boolean isReadFlag() {
        return readFlag;
    }

    public void setReadFlag(boolean readFlag) {
        this.readFlag = readFlag;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
