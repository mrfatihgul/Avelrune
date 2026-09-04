package com.avelrune.inquiry.service.impl;

import com.avelrune.inquiry.client.PropertyClient;
import com.avelrune.inquiry.dto.InquiryRequest;
import com.avelrune.inquiry.dto.InquiryResponse;
import com.avelrune.inquiry.dto.PropertyRef;
import com.avelrune.inquiry.entity.Inquiry;
import com.avelrune.inquiry.exception.ApiException;
import com.avelrune.inquiry.repository.InquiryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
