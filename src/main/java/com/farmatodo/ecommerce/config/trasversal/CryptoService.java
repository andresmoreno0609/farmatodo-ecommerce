package com.farmatodo.ecommerce.config.trasversal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CryptoService {

    private static final int GCM_TAG_BITS = 128;

    private final SecretKey key;
    private final int ivLength;
    private final String algorithm;
    private final SecureRandom random = new SecureRandom();

    public CryptoService(
            SecretKey key,
            @Value("${security.crypto.iv-length:12}") int ivLength,
            @Value("${security.crypto.algorithm:AES/GCM/NoPadding}") String algorithm) {
        this.key = key;
        this.ivLength = ivLength;
        this.algorithm = algorithm;
        if (ivLength < 12 || ivLength > 16) {
            throw new IllegalArgumentException("IV length recomendado entre 12 y 16 bytes para GCM. Actual: " + ivLength);
        }
    }

    public String encrypt(String plain) {
        try {
            byte[] iv = new byte[ivLength];
            random.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] cipherText = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));

            // Empaquetamos IV + ciphertext en un solo Base64 (simple de almacenar)
            ByteBuffer bb = ByteBuffer.allocate(iv.length + cipherText.length);
            bb.put(iv);
            bb.put(cipherText);
            return Base64.getEncoder().encodeToString(bb.array());
        } catch (Exception e) {
            throw new IllegalStateException("Error cifrando datos", e);
        }
    }

    public String decrypt(String base64) {
        try {
            byte[] all = Base64.getDecoder().decode(base64);
            byte[] iv = new byte[ivLength];
            byte[] cipherText = new byte[all.length - ivLength];
            System.arraycopy(all, 0, iv, 0, ivLength);
            System.arraycopy(all, ivLength, cipherText, 0, cipherText.length);

            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] plain = cipher.doFinal(cipherText);
            return new String(plain, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Error descifrando datos", e);
        }
    }
}
