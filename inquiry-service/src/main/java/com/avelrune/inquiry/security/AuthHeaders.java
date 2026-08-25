package com.avelrune.inquiry.security;

import com.avelrune.inquiry.exception.ApiException;

public final class AuthHeaders {

    private AuthHeaders() {
    }

    public static void requireAdmin(String role) {
        if (!isAdmin(role)) {
            throw new ApiException(403, "Bu işlem için yetkiniz yok.");
        }
    }

    public static Long requireUserId(String raw) {
        Long id = optionalUserId(raw);
        if (id == null) {
            throw new ApiException(401, "Giriş yapmanız gerekiyor.");
        }
        return id;
    }

    public static Long optionalUserId(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(raw.trim());
        } catch (NumberFormatException ex) {
            throw new ApiException(401, "Giriş yapmanız gerekiyor.");
        }
    }

    public static boolean isAdmin(String role) {
        return "ADMIN".equals(role);
    }
}
