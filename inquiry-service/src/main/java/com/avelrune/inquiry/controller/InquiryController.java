package com.avelrune.inquiry.controller;

import com.avelrune.inquiry.dto.InquiryRequest;
import com.avelrune.inquiry.dto.InquiryResponse;
import com.avelrune.inquiry.dto.InquiryStatsResponse;
import com.avelrune.inquiry.dto.PropertyMessageSummary;
import com.avelrune.inquiry.security.AuthHeaders;
import com.avelrune.inquiry.service.InquiryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inquiries")
public class InquiryController {

    private final InquiryService inquiryService;

    public InquiryController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    @PostMapping
    public ResponseEntity<InquiryResponse> create(
            @Valid @RequestBody InquiryRequest request,
            @RequestHeader(value = "X-Authenticated-User-Id", required = false) String userId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inquiryService.create(request, AuthHeaders.optionalUserId(userId)));
    }

    @GetMapping
    public ResponseEntity<List<InquiryResponse>> list(
            @RequestHeader(value = "X-Authenticated-Role", required = false) String role
    ) {
        AuthHeaders.requireAdmin(role);
        return ResponseEntity.ok(inquiryService.list());
    }

    @GetMapping("/summary")
    public ResponseEntity<List<PropertyMessageSummary>> summary(
            @RequestHeader(value = "X-Authenticated-User-Id", required = false) String userId
    ) {
        return ResponseEntity.ok(inquiryService.summaryForOwner(AuthHeaders.requireUserId(userId)));
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<InquiryResponse>> forProperty(
            @PathVariable Long propertyId,
            @RequestHeader(value = "X-Authenticated-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Authenticated-Role", required = false) String role
    ) {
        return ResponseEntity.ok(inquiryService.listForProperty(
                propertyId,
                AuthHeaders.optionalUserId(userId),
                AuthHeaders.isAdmin(role)
        ));
    }

    @GetMapping("/stats")
    public ResponseEntity<InquiryStatsResponse> stats(
            @RequestHeader(value = "X-Authenticated-Role", required = false) String role
    ) {
        AuthHeaders.requireAdmin(role);
        return ResponseEntity.ok(inquiryService.stats());
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<InquiryResponse> markRead(
            @PathVariable Long id,
            @RequestHeader(value = "X-Authenticated-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Authenticated-Role", required = false) String role
    ) {
        return ResponseEntity.ok(inquiryService.markRead(
                id,
                AuthHeaders.optionalUserId(userId),
                AuthHeaders.isAdmin(role)
        ));
    }
}
