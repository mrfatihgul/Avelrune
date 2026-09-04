package com.avelrune.gateway.security;

import org.springframework.http.HttpMethod;

final class GatewayAccess {

    private GatewayAccess() {
    }

    static boolean isPublic(HttpMethod method, String path) {
        if (path.startsWith("/api/docs/") || path.startsWith("/actuator")) {
            return true;
        }
        if (method == HttpMethod.OPTIONS) {
            return true;
        }
        if (method == HttpMethod.POST && (pathEquals(path, "/api/auth/login") || pathEquals(path, "/api/auth/register"))) {
            return true;
        }
        if (pathEquals(path, "/api/properties/mine") || path.startsWith("/api/properties/owned")) {
            return false;
        }
        if (isOptionalJwt(method, path)) {
            return true;
        }
        if (isRead(method) && (pathEquals(path, "/api/properties") || path.startsWith("/api/properties/"))) {
            return true;
        }
        if (isRead(method) && (pathEquals(path, "/api/agents") || path.startsWith("/api/agents/"))) {
            return true;
        }
        return false;
    }

    static boolean requiresAdmin(HttpMethod method, String path) {
        if (path.startsWith("/api/admin") || path.startsWith("/api/auth/users")) {
            return true;
        }
        if (pathEquals(path, "/api/inquiries/stats")) {
            return true;
        }
        return isRead(method) && pathEquals(path, "/api/inquiries");
    }

    static boolean isOptionalJwt(HttpMethod method, String path) {
        return method == HttpMethod.POST && pathEquals(path, "/api/inquiries");
    }

    static boolean isRead(HttpMethod method) {
        return method == HttpMethod.GET || method == HttpMethod.HEAD;
    }

    static boolean pathEquals(String path, String expected) {
        return path.equals(expected) || path.equals(expected + "/");
    }
}
