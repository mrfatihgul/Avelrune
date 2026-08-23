package com.avelrune.auth.security;

public interface JwtService {

    String generateToken(Long userId, String email, String role);

    long getExpirationMs();
}
