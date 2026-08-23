package com.avelrune.property.dto;

public class AgentResponse {

    private Long id;
    private String fullName;
    private String title;
    private String bio;
    private String phone;
    private String email;
    private String photoUrl;
    private String expertise;

    public AgentResponse(
            Long id,
            String fullName,
            String title,
            String bio,
            String phone,
            String email,
            String photoUrl,
            String expertise
    ) {
        this.id = id;
        this.fullName = fullName;
        this.title = title;
        this.bio = bio;
        this.phone = phone;
        this.email = email;
        this.photoUrl = photoUrl;
        this.expertise = expertise;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getTitle() {
        return title;
    }

    public String getBio() {
        return bio;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getExpertise() {
        return expertise;
    }
}
