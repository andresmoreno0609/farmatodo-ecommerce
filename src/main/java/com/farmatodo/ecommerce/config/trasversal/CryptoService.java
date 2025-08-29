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
    private static final int IV_BYTES = 12;     // Recomendado para GCM
    private static final int TAG_BITS = 128;    // 16 bytes de tag

    private final SecretKey key;
    private final SecureRandom rng = new SecureRandom();

    /**
     * Soporta la nueva propiedad crypto.keyB64 (preferida) y la antigua crypto.key (legado).
     * Pasa un solo valor por env: CRYPTO_KEY_B64 o (en legado) CRYPTO_KEY.
     */
    public CryptoService(
            @Value("${crypto.keyB64:#{null}}") String keyB64,
            @Value("${crypto.key:#{null}}") String legacyKeyB64
    ) {
        String base64 = (keyB64 != null && !keyB64.isBlank()) ? keyB64 : legacyKeyB64;

        if (base64 == null || base64.isBlank()) {
            throw new IllegalStateException(
                    "Missing AES key. Set property 'crypto.keyB64' (preferred) " +
                            "or legacy 'crypto.key' with a Base64-encoded 16/24/32-byte key.");
        }

        byte[] raw;
        try {
            raw = Base64.getDecoder().decode(base64.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("The provided crypto key is not valid Base64.", e);
        }

        int len = raw.length;
        if (len != 16 && len != 24 && len != 32) {
            throw new IllegalStateException("AES key must be 16/24/32 bytes (got " + len + ").");
        }

        log.info("Crypto key loaded ({} bytes). Using AES/GCM.", len);
        this.key = new SecretKeySpec(raw, ALG);
    }

    /** Cifra y devuelve Base64( IV || CIPHERTEXT ) */
    public String encrypt(String plaintext) {
        try {
            byte[] iv = new byte[IV_BYTES];
            rng.nextBytes(iv);

            Cipher c = Cipher.getInstance(TRANS);
            c.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            byte[] ct = c.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            ByteBuffer bb = ByteBuffer.allocate(iv.length + ct.length);
            bb.put(iv).put(ct);
            return Base64.getEncoder().encodeToString(bb.array());
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Encrypt error", e);
        }
    }

    /** Descifra Base64( IV || CIPHERTEXT ) */
    public String decrypt(String base64IvPlusCiphertext) {
        try {
            byte[] ivPlusCt = Base64.getDecoder().decode(base64IvPlusCiphertext);
            if (ivPlusCt.length < IV_BYTES + 16) { // IV + tag mínimo
                throw new IllegalArgumentException("Ciphertext too short.");
            }
            byte[] iv = new byte[IV_BYTES];
            byte[] ct = new byte[ivPlusCt.length - IV_BYTES];
            System.arraycopy(ivPlusCt, 0, iv, 0, IV_BYTES);
            System.arraycopy(ivPlusCt, IV_BYTES, ct, 0, ct.length);

            Cipher c = Cipher.getInstance(TRANS);
            c.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            byte[] pt = c.doFinal(ct);
            return new String(pt, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Decrypt error", e);
        }
    }
}
