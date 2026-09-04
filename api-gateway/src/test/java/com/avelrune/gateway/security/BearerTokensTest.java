package com.avelrune.gateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BearerTokensTest {

    @Test
    void stripsDuplicateBearerQuotesAndLineBreaks() {
        String jwt = "eyJhbGciOiJSUzI1NiJ9.aaa.bbb";
        assertEquals(jwt, BearerTokens.normalize("Bearer " + jwt));
        assertEquals(jwt, BearerTokens.normalize("bearer Bearer " + jwt));
        assertEquals(jwt, BearerTokens.normalize("\"" + jwt + "\""));
        assertEquals(jwt, BearerTokens.normalize("Bearer \"Bearer " + jwt + "\""));
        assertEquals(jwt, BearerTokens.normalize("Bearer eyJhbGciOiJSUzI1NiJ9.\naaa.bbb"));
    }

    @Test
    void prefersJwtWhenAnEmptyBearerHeaderComesFirst() {
        String jwt = "eyJhbGciOiJSUzI1NiJ9.aaa.bbb";
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer ");
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
        assertEquals(jwt, BearerTokens.extract(headers));
    }

    @Test
    void treatsUnresolvedPostmanPlaceholderAsMissing() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer {{token}}");
        assertNull(BearerTokens.extract(headers));
    }

    @Test
    void readsAccessTokenHeaderWhenAuthorizationIsEmpty() {
        String jwt = "eyJhbGciOiJSUzI1NiJ9.aaa.bbb";
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer {{token}}");
        headers.add(BearerTokens.ACCESS_TOKEN_HEADER, jwt);
        assertEquals(jwt, BearerTokens.extract(headers));
    }

    @Test
    void prefersAccessTokenHeaderOverStaleAuthorization() {
        String fresh = "eyJhbGciOiJSUzI1NiJ9.fresh.sig";
        String stale = "eyJhbGciOiJSUzI1NiJ9.stale.sig";
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + stale);
        headers.add(BearerTokens.ACCESS_TOKEN_HEADER, fresh);
        assertEquals(fresh, BearerTokens.candidates(headers).get(0));
    }
}
