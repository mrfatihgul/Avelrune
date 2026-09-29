package com.avelrune.auth.security.impl;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class RsaKeyServiceImplTest {
    @TempDir Path dir;

    @Test
    void generatedKeysKeepLegacyFormatsAndJwtContractAcrossRestart() throws Exception {
        var keys = new RsaKeyServiceImpl(dir.toString());
        assertEquals(2048, ((RSAPublicKey) keys.getPublicKey()).getModulus().bitLength());
        String privatePem = Files.readString(dir.resolve("private.pem"));
        String publicPem = Files.readString(dir.resolve("public.pem"));
        assertTrue(privatePem.startsWith("-----BEGIN PRIVATE KEY-----"));
        assertTrue(publicPem.startsWith("-----BEGIN PUBLIC KEY-----"));
        // The previous JDK-only readers must also understand newly generated files.
        var factory = KeyFactory.getInstance("RSA");
        assertArrayEquals(keys.getPrivateKey().getEncoded(), factory.generatePrivate(
                new PKCS8EncodedKeySpec(decode(privatePem, "PRIVATE KEY"))).getEncoded());
        var jdkPublic = factory.generatePublic(new X509EncodedKeySpec(decode(publicPem, "PUBLIC KEY")));
        var jwt = new JwtServiceImpl(keys, 28800000);
        String token = jwt.generateToken(42L, "user@example.test", "USER");
        var parsed = Jwts.parser().verifyWith(jdkPublic).build().parseSignedClaims(token);
        assertEquals("RS256", parsed.getHeader().getAlgorithm());
        assertEquals("user@example.test", parsed.getPayload().getSubject());
        assertEquals(42L, parsed.getPayload().get("uid", Long.class));
        assertEquals("USER", parsed.getPayload().get("role"));
        assertEquals(28800000, parsed.getPayload().getExpiration().getTime()
                - parsed.getPayload().getIssuedAt().getTime());
        var restarted = new RsaKeyServiceImpl(dir.toString());
        assertEquals(privatePem, Files.readString(dir.resolve("private.pem")));
        assertEquals(publicPem, Files.readString(dir.resolve("public.pem")));
        assertDoesNotThrow(() -> Jwts.parser().verifyWith(restarted.getPublicKey()).build().parseSignedClaims(token));
    }

    @Test
    void readsExistingJdkKeysWithoutRewritingAndAcceptsOldToken() throws Exception {
        var generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        var pair = generator.generateKeyPair();
        String privatePem = legacyPem("PRIVATE KEY", pair.getPrivate().getEncoded());
        String publicPem = legacyPem("PUBLIC KEY", pair.getPublic().getEncoded());
        Files.writeString(dir.resolve("private.pem"), privatePem);
        Files.writeString(dir.resolve("public.pem"), publicPem);
        String oldToken = Jwts.builder().subject("admin@example.test").claim("role", "ADMIN")
                .signWith(pair.getPrivate(), Jwts.SIG.RS256).compact();
        var loaded = new RsaKeyServiceImpl(dir.toString());
        assertArrayEquals(pair.getPrivate().getEncoded(), loaded.getPrivateKey().getEncoded());
        assertEquals(privatePem, Files.readString(dir.resolve("private.pem")));
        assertEquals(publicPem, Files.readString(dir.resolve("public.pem")));
        assertDoesNotThrow(() -> Jwts.parser().verifyWith(loaded.getPublicKey()).build().parseSignedClaims(oldToken));
    }

    @Test
    void incompletePairIsNeverOverwritten() throws Exception {
        Files.writeString(dir.resolve("private.pem"), "keep existing private key");
        assertThrows(IllegalStateException.class, () -> new RsaKeyServiceImpl(dir.toString()));
        assertEquals("keep existing private key", Files.readString(dir.resolve("private.pem")));
        assertFalse(Files.exists(dir.resolve("public.pem")));
    }

    @Test
    void mismatchedPairIsRejectedWithoutRewriting() throws Exception {
        new RsaKeyServiceImpl(dir.toString());
        Path other = dir.resolve("other");
        new RsaKeyServiceImpl(other.toString());
        String wrongPublic = Files.readString(other.resolve("public.pem"));
        Files.writeString(dir.resolve("public.pem"), wrongPublic);
        assertThrows(IllegalStateException.class, () -> new RsaKeyServiceImpl(dir.toString()));
        assertEquals(wrongPublic, Files.readString(dir.resolve("public.pem")));
    }

    private static String legacyPem(String type, byte[] bytes) {
        return "-----BEGIN " + type + "-----\n"
                + Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(bytes)
                + "\n-----END " + type + "-----\n";
    }

    private static byte[] decode(String pem, String type) {
        return Base64.getDecoder().decode(pem.replace("-----BEGIN " + type + "-----", "")
                .replace("-----END " + type + "-----", "").replaceAll("\\s", ""));
    }
}
