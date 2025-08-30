package com.farmatodo.ecommerce.crypto;

import com.farmatodo.ecommerce.config.trasversal.CryptoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "crypto.algorithm=AES/GCM/NoPadding",
        // 32 bytes Base64 (AES-256)
        "crypto.keyB64=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
        "crypto.iv-length=12"
})
class CryptoServiceTest {

    @Autowired
    CryptoService crypto;

    @Test
    void roundTrip_encrypt_decrypt_ok() {
        String plain = "4111111111111111|123|12/29";
        String enc = crypto.encrypt(plain);
        String dec = crypto.decrypt(enc);
        assertThat(dec).isEqualTo(plain);
    }
}
