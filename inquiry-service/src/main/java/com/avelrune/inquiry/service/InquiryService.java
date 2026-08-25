package com.avelrune.inquiry.service;

import com.avelrune.inquiry.dto.InquiryRequest;
import com.avelrune.inquiry.dto.InquiryResponse;
import com.avelrune.inquiry.dto.InquiryStatsResponse;
import com.avelrune.inquiry.dto.PropertyMessageSummary;

import java.util.List;

public interface InquiryService {

    InquiryResponse create(InquiryRequest request, Long senderUserId);

    List<InquiryResponse> list();

    List<InquiryResponse> listForProperty(Long propertyId, Long actorId, boolean admin);

    List<PropertyMessageSummary> summaryForOwner(Long ownerId);

    InquiryResponse markRead(Long id, Long actorId, boolean admin);

    InquiryStatsResponse stats();
}
