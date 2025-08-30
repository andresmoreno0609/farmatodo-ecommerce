package com.farmatodo.ecommerce.config.trasversal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

@Configuration
public class CryptoConfig {

    @Bean
    public SecretKey cryptoKeyFromProperty(
            // Tu nombre actual en application.yml
            @Value("${crypto.keyB64:}") String keyB64,
            // Compatibilidad con nombre previo
            @Value("${security.crypto.key-base64:}") String legacyKeyB64
    ) {
        String effective = !keyB64.isBlank() ? keyB64 : legacyKeyB64;
        if (effective == null || effective.isBlank()) {
            throw new IllegalArgumentException(
                    "No se encontró la clave Base64. Define 'crypto.keyB64' (o 'security.crypto.key-base64').");
        }
        byte[] key = Base64.getDecoder().decode(effective.trim());
        int len = key.length; // 16, 24 o 32
        if (len != 16 && len != 24 && len != 32) {
            throw new IllegalArgumentException(
                    "La clave Base64 debe decodificar a 16/24/32 bytes (AES-128/192/256). Tamaño actual: " + len);
        }
        return new SecretKeySpec(key, "AES");
    }

    // Fallback útil para tests si no quieres definir variable en ese entorno (opcional)
    @Bean
    @ConditionalOnMissingBean(SecretKey.class)
    public SecretKey cryptoKeyFallback() {
        byte[] key = new byte[32]; // AES-256
        new SecureRandom().nextBytes(key);
        return new SecretKeySpec(key, "AES");
    }
}
