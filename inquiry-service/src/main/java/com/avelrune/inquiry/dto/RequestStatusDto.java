package com.avelrune.inquiry.dto;

import com.avelrune.inquiry.enums.InquirySituation;
import jakarta.validation.constraints.NotNull;

public class RequestStatusDto {

    @NotNull
    private InquirySituation status;

    public InquirySituation getStatus() {
        return status;
    }

    public void setStatus(InquirySituation status) {
        this.status = status;
    }
}
