package com.avelrune.request.dto;

public class InquiryStatsResponse {

    private long total;
    private long unread;

    public InquiryStatsResponse(long total, long unread) {
        this.total = total;
        this.unread = unread;
    }

    public long getTotal() {
        return total;
    }

    public long getUnread() {
        return unread;
    }
}
