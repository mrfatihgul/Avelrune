package com.avelrune.request.dto;

public class PropertyMessageSummary {

    private Long propertyId;
    private long total;
    private long unread;

    public PropertyMessageSummary(Long propertyId, long total, long unread) {
        this.propertyId = propertyId;
        this.total = total;
        this.unread = unread;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public long getTotal() {
        return total;
    }

    public long getUnread() {
        return unread;
    }
}
