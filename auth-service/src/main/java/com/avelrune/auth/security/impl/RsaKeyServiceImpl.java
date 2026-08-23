package com.avelrune.auth.security.impl;

import com.avelrune.auth.security.RsaKeyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class RsaKeyServiceImpl implements RsaKeyService {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public RsaKeyServiceImpl(@Value("${jwt.keys-dir:/app/keys}") String keysDir) {
        try {
            Path dir = Path.of(keysDir);
            Files.createDirectories(dir);
            Path privatePem = dir.resolve("private.pem");
            Path publicPem = dir.resolve("public.pem");
            if (Files.exists(privatePem) && Files.exists(publicPem)) {
                this.privateKey = readPrivate(privatePem);
                this.publicKey = readPublic(publicPem);
            } else {
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
                generator.initialize(2048);
                KeyPair pair = generator.generateKeyPair();
                writePem(privatePem, "PRIVATE KEY", pair.getPrivate().getEncoded());
                writePem(publicPem, "PUBLIC KEY", pair.getPublic().getEncoded());
                this.privateKey = pair.getPrivate();
                this.publicKey = pair.getPublic();
            }
        } catch (Exception ex) {
            throw new IllegalStateException("RSA anahtarları hazırlanamadı: " + ex.getMessage(), ex);
        }
    }

    @Override
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    @Override
    public PublicKey getPublicKey() {
        return publicKey;
    }

    private PrivateKey readPrivate(Path path) throws Exception {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodePem(path, "PRIVATE KEY"));
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    private PublicKey readPublic(Path path) throws Exception {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decodePem(path, "PUBLIC KEY"));
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    private byte[] decodePem(Path path, String type) throws Exception {
        String pem = Files.readString(path, StandardCharsets.UTF_8)
                .replace("-----BEGIN " + type + "-----", "")
                .replace("-----END " + type + "-----", "")
                .replaceAll("\\s", "");
        return Base64.getDecoder().decode(pem);
    }

    private void writePem(Path path, String type, byte[] der) throws Exception {
        String body = Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(der);
        String pem = "-----BEGIN " + type + "-----\n" + body + "\n-----END " + type + "-----\n";
        Files.writeString(path, pem, StandardCharsets.UTF_8);
    }
}
