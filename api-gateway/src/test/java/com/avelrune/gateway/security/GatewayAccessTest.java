package com.avelrune.gateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GatewayAccessTest {

    @Test
    void publicCatalogAllowsGetAndHead() {
        assertTrue(GatewayAccess.isPublic(HttpMethod.GET, "/api/properties"));
        assertTrue(GatewayAccess.isPublic(HttpMethod.HEAD, "/api/properties"));
        assertTrue(GatewayAccess.isPublic(HttpMethod.GET, "/api/properties/bebek-te-bogaz"));
        assertTrue(GatewayAccess.isPublic(HttpMethod.HEAD, "/api/properties/bebek-te-bogaz"));
        assertTrue(GatewayAccess.isPublic(HttpMethod.HEAD, "/api/agents"));
        assertTrue(GatewayAccess.isPublic(HttpMethod.GET, "/api/properties/media/photo.jpg"));
        assertFalse(GatewayAccess.isPublic(HttpMethod.POST, "/api/properties/media"));
    }

    @Test
    void mineAndOwnedStayPrivateForHead() {
        assertFalse(GatewayAccess.isPublic(HttpMethod.HEAD, "/api/properties/mine"));
        assertFalse(GatewayAccess.isPublic(HttpMethod.GET, "/api/properties/mine"));
        assertFalse(GatewayAccess.isPublic(HttpMethod.HEAD, "/api/properties/owned/1"));
    }

    @Test
    void inquiryListRequiresAdminForGetAndHead() {
        assertTrue(GatewayAccess.requiresAdmin(HttpMethod.GET, "/api/inquiries"));
        assertTrue(GatewayAccess.requiresAdmin(HttpMethod.HEAD, "/api/inquiries"));
        assertFalse(GatewayAccess.isPublic(HttpMethod.HEAD, "/api/inquiries"));
    }

    @Test
    void deletingAUserRequiresAdmin() {
        assertFalse(GatewayAccess.isPublic(HttpMethod.DELETE, "/api/auth/users/2"));
        assertTrue(GatewayAccess.requiresAdmin(HttpMethod.DELETE, "/api/auth/users/2"));
    }
}
