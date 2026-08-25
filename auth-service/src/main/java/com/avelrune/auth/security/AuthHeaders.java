package com.avelrune.auth.security;

import com.avelrune.auth.exception.ApiException;

public final class AuthHeaders {

    private AuthHeaders() {
    }

    public static void requireAdmin(String role) {
        if (!isAdmin(role)) {
            throw new ApiException(403, "Bu işlem için yetkiniz yok.");
        }
    }

    public static boolean isAdmin(String role) {
        return "ADMIN".equals(role);
    }
}
