package com.avelrune.auth.security.impl;

import com.avelrune.auth.security.RsaKeyService;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator;
import org.bouncycastle.openssl.jcajce.JcaMiscPEMGenerator;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Provider;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Service
public class RsaKeyServiceImpl implements RsaKeyService {

    private static final Provider PROVIDER = new BouncyCastleProvider();
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public RsaKeyServiceImpl(@Value("${jwt.keys-dir:/app/keys}") String keysDir) {
        try {
            Path dir = Path.of(keysDir);
            Files.createDirectories(dir);
            Path privatePem = dir.resolve("private.pem");
            Path publicPem = dir.resolve("public.pem");
            if (Files.exists(privatePem) != Files.exists(publicPem)) {
                throw new IllegalStateException("RSA anahtar çiftinin bir dosyası eksik; mevcut anahtarın üzerine yazılmadı.");
            }
            if (Files.exists(privatePem) && Files.exists(publicPem)) {
                this.privateKey = readPrivate(privatePem);
                this.publicKey = readPublic(publicPem);
            } else {
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", PROVIDER);
                generator.initialize(2048);
                KeyPair pair = generator.generateKeyPair();
                // Preserve the PKCS#8 / SubjectPublicKeyInfo formats used by existing volumes.
                writePem(privatePem, new JcaPKCS8Generator(pair.getPrivate(), null));
                writePem(publicPem, new JcaMiscPEMGenerator(pair.getPublic()));
                this.privateKey = pair.getPrivate();
                this.publicKey = pair.getPublic();
            }
            if (!(privateKey instanceof RSAPrivateKey rsaPrivate)
                    || !(publicKey instanceof RSAPublicKey rsaPublic)
                    || !rsaPrivate.getModulus().equals(rsaPublic.getModulus())) {
                throw new IllegalStateException("RSA private.pem ve public.pem aynı anahtar çiftine ait olmalıdır.");
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
        Object object = readPem(path);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(PROVIDER);
        if (object instanceof PrivateKeyInfo info) {
            return converter.getPrivateKey(info);
        }
        if (object instanceof PEMKeyPair pair) {
            return converter.getKeyPair(pair).getPrivate();
        }
        throw new IllegalStateException("private.pem desteklenen bir şifresiz private key içermelidir.");
    }

    private PublicKey readPublic(Path path) throws Exception {
        if (readPem(path) instanceof SubjectPublicKeyInfo info) {
            return new JcaPEMKeyConverter().setProvider(PROVIDER).getPublicKey(info);
        }
        throw new IllegalStateException("public.pem bir public key içermelidir.");
    }

    private Object readPem(Path path) throws Exception {
        try (PEMParser parser = new PEMParser(Files.newBufferedReader(path, StandardCharsets.UTF_8))) {
            return parser.readObject();
        }
    }

    private void writePem(Path path, PemObjectGenerator key) throws Exception {
        try (JcaPEMWriter writer = new JcaPEMWriter(Files.newBufferedWriter(path, StandardCharsets.UTF_8))) {
            writer.writeObject(key);
        }
    }
}
