package com.farmatodo.ecommerce.config.trasversal;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

@Log4j2
@Component
public class CryptoService {
    private static final String ALG = "AES";
    private static final String TRANS = "AES/GCM/NoPadding";
    private static final int IV_BYTES = 12;

    private final SecretKey key;
    private final SecureRandom rng = new SecureRandom(); // compatible en Windows

    public CryptoService(@Value("${crypto.key:}") String base64Key) {
        log.info("crypto.key length(bytes) = {}", Base64.getDecoder().decode(base64Key.trim()).length);


        if (base64Key == null || base64Key.isBlank()) {
            throw new IllegalStateException("crypto.key is missing. Set CRYPTO_KEY env var or application.yml.");
        }
        byte[] k;
        try {
            k = Base64.getDecoder().decode(base64Key.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("crypto.key is not valid Base64.", e);
        }
        if (k.length != 32) { // 32 bytes = 256 bits
            throw new IllegalStateException("crypto.key must be 32 bytes (256-bit) after Base64 decode. Got " + k.length);
        }
        this.key = new SecretKeySpec(k, ALG);
    }

    public String encrypt(String plaintext) {
        try {
            byte[] iv = new byte[IV_BYTES];
            rng.nextBytes(iv);
            Cipher c = Cipher.getInstance(TRANS);
            c.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
            byte[] enc = c.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            ByteBuffer bb = ByteBuffer.allocate(iv.length + enc.length).put(iv).put(enc);
            return Base64.getEncoder().encodeToString(bb.array());
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Encrypt error", e);
        }
    }
}
