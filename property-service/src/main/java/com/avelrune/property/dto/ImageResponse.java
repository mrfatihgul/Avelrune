package com.avelrune.property.dto;

public class ImageResponse {

    private Long id;
    private String url;
    private String altText;
    private int sortOrder;

    public ImageResponse(Long id, String url, String altText, int sortOrder) {
        this.id = id;
        this.url = url;
        this.altText = altText;
        this.sortOrder = sortOrder;
    }

    public Long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getAltText() {
        return altText;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
