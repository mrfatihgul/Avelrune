package com.avelrune.property.controller;

import com.avelrune.property.dto.PropertyStatsResponse;
import com.avelrune.property.security.AuthHeaders;
import com.avelrune.property.service.PropertyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminStatsController {

    private final PropertyService propertyService;

    public AdminStatsController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping("/stats")
    public ResponseEntity<PropertyStatsResponse> stats(
            @RequestHeader(value = "X-Authenticated-Role", required = false) String role
    ) {
        AuthHeaders.requireAdmin(role);
        return ResponseEntity.ok(propertyService.stats());
    }
}
