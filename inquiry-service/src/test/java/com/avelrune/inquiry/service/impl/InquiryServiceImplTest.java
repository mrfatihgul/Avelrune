package com.avelrune.inquiry.service.impl;

import com.avelrune.inquiry.client.PropertyClient;
import com.avelrune.inquiry.dto.InquiryRequest;
import com.avelrune.inquiry.dto.InquiryResponse;
import com.avelrune.inquiry.dto.PropertyRef;
import com.avelrune.inquiry.dto.RequestStatusDto;
import com.avelrune.inquiry.entity.Inquiry;
import com.avelrune.inquiry.enums.InquirySituation;
import com.avelrune.inquiry.exception.ApiException;
import com.avelrune.inquiry.repository.InquiryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InquiryServiceImplTest {

    @Mock
    private InquiryRepository inquiryRepository;
    @Mock
    private PropertyClient propertyClient;

    private InquiryServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new InquiryServiceImpl(inquiryRepository, propertyClient);
    }

    @Test
    void createRejectsMessageToOwnListing() {
        PropertyRef property = new PropertyRef();
        property.setId(1L);
        property.setSlug("ornek-ilan");
        property.setAssignedUserId(2L);
        when(propertyClient.findById(1L)).thenReturn(property);

        ApiException ex = assertThrows(ApiException.class, () -> service.create(request(1L), 2L));
        assertEquals(400, ex.getStatus());
        assertEquals("Kendi ilanınıza mesaj gönderemezsiniz.", ex.getMessage());
        verify(inquiryRepository, never()).save(any());
    }

    @Test
    void guestCanMessageAListing() {
        PropertyRef property = new PropertyRef();
        property.setId(1L);
        property.setSlug("ornek-ilan");
        property.setAssignedUserId(2L);
        when(propertyClient.findById(1L)).thenReturn(property);
        when(inquiryRepository.save(any(Inquiry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InquiryResponse response = service.create(request(1L), null);
        assertEquals(1L, response.getPropertyId());
        assertEquals(2L, response.getOwnerUserId());
        verify(inquiryRepository).save(any(Inquiry.class));
    }

    @Test
    void ownerCanUpdateStatus() {
        Inquiry inquiry = new Inquiry();
        inquiry.setOwnerUserId(2L);
        inquiry.setReadFlag(true);
        when(inquiryRepository.findById(1L)).thenReturn(Optional.of(inquiry));
        when(inquiryRepository.save(inquiry)).thenReturn(inquiry);

        InquiryResponse response = service.updateStatus(statusRequest(InquirySituation.SOLVED), 1L, false, 2L);

        assertEquals(InquirySituation.SOLVED, inquiry.getStatus());
        assertEquals(InquirySituation.SOLVED, response.getStatus());
        assertTrue(response.isRead());
        verify(inquiryRepository).save(inquiry);
    }

    @Test
    void ownerCanReopenSolvedInquiry() {
        Inquiry inquiry = new Inquiry();
        inquiry.setOwnerUserId(2L);
        inquiry.setStatus(InquirySituation.SOLVED);
        when(inquiryRepository.findById(1L)).thenReturn(Optional.of(inquiry));
        when(inquiryRepository.save(inquiry)).thenReturn(inquiry);

        InquiryResponse response = service.updateStatus(statusRequest(InquirySituation.UNDERREVIEW), 1L, false, 2L);

        assertEquals(InquirySituation.UNDERREVIEW, inquiry.getStatus());
        assertEquals(InquirySituation.UNDERREVIEW, response.getStatus());
        verify(inquiryRepository).save(inquiry);
    }

    @Test
    void adminCanUpdateUnassignedInquiryWithoutActorId() {
        Inquiry inquiry = new Inquiry();
        when(inquiryRepository.findById(1L)).thenReturn(Optional.of(inquiry));
        when(inquiryRepository.save(inquiry)).thenReturn(inquiry);

        InquiryResponse response = service.updateStatus(statusRequest(InquirySituation.UNSOLVED), 1L, true, null);

        assertEquals(InquirySituation.UNSOLVED, inquiry.getStatus());
        assertEquals(InquirySituation.UNSOLVED, response.getStatus());
        assertFalse(response.isRead());
        verify(inquiryRepository).save(inquiry);
    }

    @Test
    void updateStatusRejectsAnotherUser() {
        Inquiry inquiry = new Inquiry();
        inquiry.setOwnerUserId(2L);
        when(inquiryRepository.findById(1L)).thenReturn(Optional.of(inquiry));

        ApiException ex = assertThrows(ApiException.class,
                () -> service.updateStatus(statusRequest(InquirySituation.SOLVED), 1L, false, 3L));

        assertEquals(403, ex.getStatus());
        assertEquals(InquirySituation.UNDERREVIEW, inquiry.getStatus());
        verify(inquiryRepository, never()).save(any());
    }

    @Test
    void updateStatusRejectsMissingActorId() {
        Inquiry inquiry = new Inquiry();
        inquiry.setOwnerUserId(2L);
        when(inquiryRepository.findById(1L)).thenReturn(Optional.of(inquiry));

        ApiException ex = assertThrows(ApiException.class,
                () -> service.updateStatus(statusRequest(InquirySituation.SOLVED), 1L, false, null));

        assertEquals(403, ex.getStatus());
        assertEquals(InquirySituation.UNDERREVIEW, inquiry.getStatus());
        verify(inquiryRepository, never()).save(any());
    }

    @Test
    void nonAdminCannotUpdateUnassignedInquiry() {
        Inquiry inquiry = new Inquiry();
        when(inquiryRepository.findById(1L)).thenReturn(Optional.of(inquiry));

        ApiException ex = assertThrows(ApiException.class,
                () -> service.updateStatus(statusRequest(InquirySituation.SOLVED), 1L, false, 2L));

        assertEquals(403, ex.getStatus());
        assertEquals(InquirySituation.UNDERREVIEW, inquiry.getStatus());
        verify(inquiryRepository, never()).save(any());
    }

    @Test
    void updateStatusRejectsMissingInquiry() {
        when(inquiryRepository.findById(1L)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> service.updateStatus(statusRequest(InquirySituation.SOLVED), 1L, true, 2L));

        assertEquals(404, ex.getStatus());
        assertEquals("Talep bulunamadı.", ex.getMessage());
        verify(inquiryRepository, never()).save(any());
    }

    private static RequestStatusDto statusRequest(InquirySituation status) {
        RequestStatusDto request = new RequestStatusDto();
        request.setStatus(status);
        return request;
    }

    private static InquiryRequest request(Long propertyId) {
        InquiryRequest request = new InquiryRequest();
        request.setFullName("Mehmet Demir");
        request.setPhone("05321234567");
        request.setEmail("mehmet.demir@example.com");
        request.setMessage("Bu ilani yerinde gormek istiyorum.");
        request.setPropertyId(propertyId);
        return request;
    }
}