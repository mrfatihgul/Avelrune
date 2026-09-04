package com.avelrune.gateway.security.impl;

import com.avelrune.gateway.security.JwtPublicKeyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class JwtPublicKeyServiceImpl implements JwtPublicKeyService {

    private final Path path;
    private volatile RSAPublicKey publicKey;

    public JwtPublicKeyServiceImpl(@Value("${jwt.public-key-path:/app/keys/public.pem}") String publicKeyPath) {
        this.path = Path.of(publicKeyPath);
        this.publicKey = read(this.path);
    }

    @Override
    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public synchronized void reload() {
        this.publicKey = read(path);
    }

    private static RSAPublicKey read(Path path) {
        try {
            if (!Files.exists(path)) {
                throw new IllegalStateException("public.pem bulunamadı: " + path);
            }
            String pem = Files.readString(path, StandardCharsets.UTF_8)
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.getDecoder().decode(pem));
            PublicKey key = KeyFactory.getInstance("RSA").generatePublic(spec);
            return (RSAPublicKey) key;
        } catch (IllegalStateException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("JWT public key okunamadı: " + ex.getMessage(), ex);
        }
    }
}
