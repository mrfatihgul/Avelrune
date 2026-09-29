package com.avelrune.gateway.security;

import com.avelrune.gateway.security.impl.JwtPublicKeyServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class JwtPemCompatibilityTest {
    @TempDir Path dir;

    @Test
    void postmanBearerAndAccessTokenHeadersPassWithRealBcSignedJwt() throws Exception {
        var pair = newPair();
        writePublic(pair);
        var service = new JwtPublicKeyServiceImpl(dir.resolve("public.pem").toString());
        var filter = new JwtGatewayFilter(service, new ObjectMapper());
        String token = token(pair, "ADMIN", Instant.now().plusSeconds(3600));
        for (String header : new String[]{"Authorization", "X-Access-Token"}) {
            var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/auth/me")
                    .header(header, header.equals("Authorization") ? "Bearer " + token : token)
                    .header("X-Authenticated-User", "spoofed"));
            AtomicReference<String> identity = new AtomicReference<>();
            filter.filter(exchange, next -> {
                identity.set(next.getRequest().getHeaders().getFirst("X-Authenticated-User"));
                assertEquals("42", next.getRequest().getHeaders().getFirst("X-Authenticated-User-Id"));
                assertEquals("ADMIN", next.getRequest().getHeaders().getFirst("X-Authenticated-Role"));
                return Mono.empty();
            }).block();
            assertEquals("admin@example.test", identity.get());
        }
    }

    @Test
    void legacyPemAndTokenRemainValid() throws Exception {
        var generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        var pair = generator.generateKeyPair();
        Files.writeString(dir.resolve("public.pem"), "-----BEGIN PUBLIC KEY-----\n"
                + Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(pair.getPublic().getEncoded())
                + "\n-----END PUBLIC KEY-----\n");
        var service = new JwtPublicKeyServiceImpl(dir.resolve("public.pem").toString());
        assertDoesNotThrow(() -> Jwts.parser().verifyWith(service.getPublicKey()).build()
                .parseSignedClaims(token(pair, "ADMIN", Instant.now().plusSeconds(3600))));
    }

    @Test
    void reloadAcceptsNewKeyAndPreservesOldKeyOnInvalidFile() throws Exception {
        writePublic(newPair());
        var service = new JwtPublicKeyServiceImpl(dir.resolve("public.pem").toString());
        var replacement = newPair();
        writePublic(replacement);
        service.reload();
        assertArrayEquals(replacement.getPublic().getEncoded(), service.getPublicKey().getEncoded());
        Files.writeString(dir.resolve("public.pem"), "invalid");
        assertThrows(IllegalStateException.class, service::reload);
        assertArrayEquals(replacement.getPublic().getEncoded(), service.getPublicKey().getEncoded());
    }

    @Test
    void invalidExpiredAndNonAdminTokensAreStillRejected() throws Exception {
        var pair = newPair();
        writePublic(pair);
        var filter = new JwtGatewayFilter(new JwtPublicKeyServiceImpl(dir.resolve("public.pem").toString()), new ObjectMapper());
        assertRejected(filter, token(newPair(), "ADMIN", Instant.now().plusSeconds(3600)), 401);
        assertRejected(filter, token(pair, "ADMIN", Instant.now().minusSeconds(1200)), 401);
        assertRejected(filter, token(pair, "USER", Instant.now().plusSeconds(3600)), 403);
        assertRejected(filter, "{{token}}", 401);
    }

    private void assertRejected(JwtGatewayFilter filter, String token, int status) {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/admin/stats")
                .header("Authorization", "Bearer " + token));
        filter.filter(exchange, next -> { fail("Unauthorized request forwarded"); return Mono.empty(); }).block();
        assertEquals(status, exchange.getResponse().getStatusCode().value());
    }

    private KeyPair newPair() throws Exception {
        var generator = KeyPairGenerator.getInstance("RSA", new BouncyCastleProvider());
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    private void writePublic(KeyPair pair) throws Exception {
        try (var writer = new JcaPEMWriter(Files.newBufferedWriter(dir.resolve("public.pem")))) {
            writer.writeObject(SubjectPublicKeyInfo.getInstance(pair.getPublic().getEncoded()));
        }
    }

    private String token(KeyPair pair, String role, Instant expiration) {
        return Jwts.builder().subject("admin@example.test").claim("uid", 42L).claim("role", role)
                .expiration(Date.from(expiration)).signWith(pair.getPrivate(), Jwts.SIG.RS256).compact();
    }
}
