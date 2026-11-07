package com.avelrune.inquiry.controller;

import com.avelrune.inquiry.dto.InquiryResponse;
import com.avelrune.inquiry.dto.RequestStatusDto;
import com.avelrune.inquiry.enums.InquirySituation;
import com.avelrune.inquiry.exception.ApiException;
import com.avelrune.inquiry.exception.GlobalExceptionHandler;
import com.avelrune.inquiry.service.InquiryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class InquiryControllerTest {

    @Mock
    private InquiryService inquiryService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Controller, JSON validation and exception handling are tested without the gateway or database.
        mockMvc = MockMvcBuilders.standaloneSetup(new InquiryController(inquiryService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @ParameterizedTest
    @EnumSource(InquirySituation.class)
    void updateStatusAcceptsEachValidStatus(InquirySituation requestedStatus) throws Exception {
        when(inquiryService.updateStatus(any(RequestStatusDto.class), eq(1L), eq(false), eq(2L)))
                .thenReturn(response(requestedStatus));

        mockMvc.perform(patch("/api/inquiries/1/update")
                        .header("X-Authenticated-User-Id", "2")
                        .header("X-Authenticated-Role", "USER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"" + requestedStatus.name() + "\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value(requestedStatus.name()));

        verify(inquiryService).updateStatus(
                argThat(request -> request.getStatus() == requestedStatus), eq(1L), eq(false), eq(2L));
    }

    @Test
    void updateStatusPassesAdminRoleAndMissingUserIdToService() throws Exception {
        when(inquiryService.updateStatus(any(RequestStatusDto.class), eq(1L), eq(true), isNull()))
                .thenReturn(response(InquirySituation.SOLVED));

        mockMvc.perform(patch("/api/inquiries/1/update")
                        .header("X-Authenticated-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"SOLVED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SOLVED"));

        verify(inquiryService).updateStatus(
                argThat(request -> request.getStatus() == InquirySituation.SOLVED), eq(1L), eq(true), isNull());
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"status\":null}", "{\"status\":\"INVALID\"}", "{", ""})
    void updateStatusRejectsInvalidBodyBeforeCallingService(String body) throws Exception {
        mockMvc.perform(patch("/api/inquiries/1/update")
                        .header("X-Authenticated-User-Id", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));

        verifyNoInteractions(inquiryService);
    }

    @ParameterizedTest
    @ValueSource(ints = {403, 404})
    void updateStatusPreservesServiceErrorStatus(int errorStatus) throws Exception {
        String message = errorStatus == 403
                ? "Bu talebin durumunu değiştirmeye yetkiniz yok."
                : "Talep bulunamadı.";
        when(inquiryService.updateStatus(any(RequestStatusDto.class), eq(1L), eq(false), eq(2L)))
                .thenThrow(new ApiException(errorStatus, message));

        mockMvc.perform(patch("/api/inquiries/1/update")
                        .header("X-Authenticated-User-Id", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"SOLVED\"}"))
                .andExpect(status().is(errorStatus))
                .andExpect(jsonPath("$.status").value(errorStatus))
                .andExpect(jsonPath("$.message").value(message));
    }

    private static InquiryResponse response(InquirySituation status) {
        return new InquiryResponse(
                1L, "Mehmet Demir", "05321234567", "mehmet.demir@example.com",
                "Bu ilanı yerinde görmek istiyorum.", "ornek-ilan", 10L, 2L, null,
                false, false, null, status
        );
    }
}
