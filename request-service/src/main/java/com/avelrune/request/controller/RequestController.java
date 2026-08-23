package com.avelrune.request.controller;

import com.avelrune.request.dto.InquiryRequest;
import com.avelrune.request.dto.InquiryResponse;
import com.avelrune.request.dto.InquiryStatsResponse;
import com.avelrune.request.dto.PropertyMessageSummary;
import com.avelrune.request.exception.ApiException;
import com.avelrune.request.service.InquiryService;
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
@RequestMapping("/api/requests")
public class RequestController {

    private final InquiryService inquiryService;

    public RequestController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    @PostMapping
    public ResponseEntity<InquiryResponse> create(
            @Valid @RequestBody InquiryRequest request,
            @RequestHeader(value = "X-Authenticated-User-Id", required = false) String userId
    ) {
        Long senderId = userId == null || userId.isBlank() ? null : Long.parseLong(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(inquiryService.create(request, senderId));
    }

    @GetMapping
    public ResponseEntity<List<InquiryResponse>> list() {
        return ResponseEntity.ok(inquiryService.list());
    }

    @GetMapping("/summary")
    public ResponseEntity<List<PropertyMessageSummary>> summary(
            @RequestHeader(value = "X-Authenticated-User-Id", required = false) String userId
    ) {
        if (userId == null || userId.isBlank()) {
            throw new ApiException(401, "Giriş yapmanız gerekiyor.");
        }
        return ResponseEntity.ok(inquiryService.summaryForOwner(Long.parseLong(userId)));
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<InquiryResponse>> forProperty(
            @PathVariable Long propertyId,
            @RequestHeader(value = "X-Authenticated-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Authenticated-Role", required = false) String role
    ) {
        Long actorId = userId == null || userId.isBlank() ? null : Long.parseLong(userId);
        return ResponseEntity.ok(inquiryService.listForProperty(propertyId, actorId, "ADMIN".equals(role)));
    }

    @GetMapping("/stats")
    public ResponseEntity<InquiryStatsResponse> stats() {
        return ResponseEntity.ok(inquiryService.stats());
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<InquiryResponse> markRead(
            @PathVariable Long id,
            @RequestHeader(value = "X-Authenticated-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Authenticated-Role", required = false) String role
    ) {
        Long actorId = userId == null || userId.isBlank() ? null : Long.parseLong(userId);
        return ResponseEntity.ok(inquiryService.markRead(id, actorId, "ADMIN".equals(role)));
    }
}
