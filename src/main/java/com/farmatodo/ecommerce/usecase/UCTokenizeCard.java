package com.farmatodo.ecommerce.usecase;

import com.farmatodo.ecommerce.DTOs.Request.TokenizacionRequest;
import com.farmatodo.ecommerce.DTOs.Response.TokenizacionResponse;
import com.farmatodo.ecommerce.adapter.TokenizeCardAdapter;
import com.farmatodo.ecommerce.config.trasversal.CryptoService;
import com.farmatodo.ecommerce.entity.TokenizedCardEntity;
import com.farmatodo.ecommerce.enums.EStatusTokenizacion;
import com.farmatodo.ecommerce.exceptions.TokenizationRejectedException;
import com.farmatodo.ecommerce.repository.TokenizedCardRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;

@Service
public class UCTokenizeCard implements TokenizeCardAdapter {

    private final TokenizedCardRepository repo;
    private final CryptoService crypto;
    private final double rejectionProb;
    private final SecureRandom rnd = new SecureRandom();

    public UCTokenizeCard(TokenizedCardRepository repo,
                          CryptoService crypto,
                          @Value("${tokenization.rejectionProbability:0.2}") double rejectionProb) {
        this.repo = repo; this.crypto = crypto; this.rejectionProb = rejectionProb;
    }

    @Override
    public TokenizacionResponse execute(TokenizacionRequest r, String correlationId) {

        if (rnd.nextDouble() < rejectionProb) {
            throw new TokenizationRejectedException("Tokenización rechazada por probabilidad");
        }

        // 2) Token + máscara + marca
        String token = UUID.randomUUID().toString().replace("-", "");
        String pan = r.numeroTarjeta();
        String last4 = pan.substring(pan.length() - 4);
        String panMask = "*".repeat(pan.length() - 4) + last4;
        String brand = pan.startsWith("4") ? "VISA" : pan.startsWith("5") ? "MASTERCARD" : "UNKNOWN";

        // 3) Cifrar payload sensible
        String json = """
      {"number":"%s","cvv":"%s","expiration":"%s","holder":"%s"}
      """.formatted(r.numeroTarjeta(), r.cvv(), r.expiracion(), r.titular());
        String encrypted = crypto.encrypt(json);

        // 4) Persistir
        TokenizedCardEntity e = new TokenizedCardEntity();
        e.setToken(token); e.setLast4(last4); e.setBrand(brand); e.setEncryptedPayload(encrypted);
        repo.save(e);

        // 5) Respuesta segura
        return new TokenizacionResponse(
                token, panMask, brand, e.getCreatedAt(), correlationId, EStatusTokenizacion.APROBADA
        );
    }
}
