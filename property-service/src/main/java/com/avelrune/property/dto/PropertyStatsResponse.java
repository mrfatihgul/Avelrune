package com.avelrune.property.dto;

public class PropertyStatsResponse {

    private long total;
    private long active;
    private long inactive;
    private long sale;
    private long rent;
    private long featured;

    public PropertyStatsResponse(long total, long active, long inactive, long sale, long rent, long featured) {
        this.total = total;
        this.active = active;
        this.inactive = inactive;
        this.sale = sale;
        this.rent = rent;
        this.featured = featured;
    }

    public long getTotal() { return total; }
    public long getActive() { return active; }
    public long getInactive() { return inactive; }
    public long getSale() { return sale; }
    public long getRent() { return rent; }
    public long getFeatured() { return featured; }
}
