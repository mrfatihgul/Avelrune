package com.avelrune.gateway.security.impl;

import com.avelrune.gateway.security.JwtPublicKeyService;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Provider;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;

@Service
public class JwtPublicKeyServiceImpl implements JwtPublicKeyService {

    private static final Provider PROVIDER = new BouncyCastleProvider();
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
            try (PEMParser parser = new PEMParser(Files.newBufferedReader(path, StandardCharsets.UTF_8))) {
                if (!(parser.readObject() instanceof SubjectPublicKeyInfo info)) {
                    throw new IllegalStateException("public.pem bir public key içermelidir.");
                }
                PublicKey key = new JcaPEMKeyConverter().setProvider(PROVIDER).getPublicKey(info);
                if (!(key instanceof RSAPublicKey rsaKey)) {
                    throw new IllegalStateException("JWT public key RSA olmalıdır.");
                }
                return rsaKey;
            }
        } catch (IllegalStateException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("JWT public key okunamadı: " + ex.getMessage(), ex);
        }
    }
}
