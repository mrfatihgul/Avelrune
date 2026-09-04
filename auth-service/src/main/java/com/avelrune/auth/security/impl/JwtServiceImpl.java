package com.avelrune.auth.security.impl;

import com.avelrune.auth.security.JwtService;
import com.avelrune.auth.security.RsaKeyService;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

    private final RsaKeyService rsaKeyService;
    private final long expirationMs;

    public JwtServiceImpl(
            RsaKeyService rsaKeyService,
            @Value("${jwt.expiration-ms}") long expirationMs
    ) {
        this.rsaKeyService = rsaKeyService;
        this.expirationMs = expirationMs;
    }

    @Override
    public String generateToken(Long userId, String email, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .claim("uid", userId)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(rsaKeyService.getPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    @Override
    public long getExpirationMs() {
        return expirationMs;
    }
}
