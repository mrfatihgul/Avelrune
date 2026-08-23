package com.avelrune.request.service;

import com.avelrune.request.dto.InquiryRequest;
import com.avelrune.request.dto.InquiryResponse;
import com.avelrune.request.dto.InquiryStatsResponse;
import com.avelrune.request.dto.PropertyMessageSummary;

import java.util.List;

public interface InquiryService {

    InquiryResponse create(InquiryRequest request, Long senderUserId);

    List<InquiryResponse> list();

    List<InquiryResponse> listForProperty(Long propertyId, Long actorId, boolean admin);

    List<PropertyMessageSummary> summaryForOwner(Long ownerId);

    InquiryResponse markRead(Long id, Long actorId, boolean admin);

    InquiryResponse markRead(Long id);

    InquiryStatsResponse stats();
}
