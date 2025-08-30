package com.farmatodo.ecommerce.usecase;

import com.farmatodo.ecommerce.DTOs.Request.TokenizacionRequest;
import com.farmatodo.ecommerce.config.trasversal.CryptoService;
import com.farmatodo.ecommerce.enums.EStatusTokenizacion;
import com.farmatodo.ecommerce.exceptions.TokenizationRejectedException;
import com.farmatodo.ecommerce.repository.TokenizedCardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenizationUseCaseTest {

    @Mock TokenizedCardRepository repo;
    @Mock CryptoService crypto;

    @Test
    void shouldReject_whenProbabilityTriggers() {
        UCTokenizeCard useCase = new UCTokenizeCard(repo, crypto, 1.0);

        var req = new TokenizacionRequest("4111111111111111","123","12/29","John Doe");

        assertThatThrownBy(() -> useCase.execute(req, "corr-1"))
                .isInstanceOf(TokenizationRejectedException.class);

        verify(repo, never()).save(any());
        verifyNoInteractions(crypto);
    }

    @Test
    void shouldAccept_andReturnToken() {
        UCTokenizeCard useCase = new UCTokenizeCard(repo, crypto, 0.0);

        when(crypto.encrypt(anyString())).thenReturn("cifrado");
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var req = new TokenizacionRequest("4111111111111111","123","12/29","John Doe");
        var res = useCase.execute(req, "corr-xyz");

        assertThat(res.token()).isNotBlank();
        assertThat(res.status()).isEqualTo(EStatusTokenizacion.APROBADA);
        assertThat(res.panEnmascarado()).isEqualTo("************1111");
        assertThat(res.marca()).isEqualTo("VISA");
        assertThat(res.idTransaccion()).isEqualTo("corr-xyz");

        verify(repo).save(any());
    }
}