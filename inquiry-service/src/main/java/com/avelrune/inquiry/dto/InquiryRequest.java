package com.avelrune.inquiry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class InquiryRequest {

    @NotBlank
    @Size(min = 2, max = 120)
    @Schema(example = "Mehmet Demir")
    private String fullName;

    @NotBlank
    @Size(min = 10, max = 40)
    @Schema(example = "05321234567")
    private String phone;

    @NotBlank
    @Email
    @Schema(example = "mehmet.demir@example.com")
    private String email;

    @NotBlank
    @Size(min = 10, max = 3000)
    @Schema(example = "Bu ilan icin yerinde gormek istiyorum. Hafta sonu uygun musunuz?")
    private String message;

    @Size(max = 180)
    @Schema(example = "bebek-te-bogaz-manzarali-4-1-daire")
    private String propertySlug;

    @Schema(example = "1")
    private Long propertyId;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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
}
