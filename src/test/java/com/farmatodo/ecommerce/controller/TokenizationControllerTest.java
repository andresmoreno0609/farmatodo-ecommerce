package com.farmatodo.ecommerce.controller;

import com.farmatodo.ecommerce.DTOs.Request.TokenizacionRequest;
import com.farmatodo.ecommerce.DTOs.Response.TokenizacionResponse;
import com.farmatodo.ecommerce.adapter.TokenizeCardAdapter;
import com.farmatodo.ecommerce.enums.EStatusTokenizacion;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TokenizationController.class)
@AutoConfigureMockMvc(addFilters = false)
class TokenizationControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean TokenizeCardAdapter adapter;

    private TokenizacionResponse okResponse(String token, String corr) {
        return new TokenizacionResponse(
                token,
                "************1111",
                "VISA",
                Instant.parse("2025-08-30T12:00:00Z"),
                corr,
                EStatusTokenizacion.APROBADA
        );
    }

    @Test
    void tokenize_shouldReturn201_withLocation_andBody() throws Exception {
        var req = new TokenizacionRequest("4111111111111111", "123", "12/29", "John Doe");
        var res = okResponse("tok_abc", "corr-provided");

        ArgumentCaptor<String> corrCap = ArgumentCaptor.forClass(String.class);
        when(adapter.execute(any(TokenizacionRequest.class), corrCap.capture())).thenReturn(res);

        mvc.perform(post("/api/v1/tokenize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .header("X-CORRELATION-ID", "corr-provided"))
                //.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/tokenize/tok_abc"))
                .andExpect(jsonPath("$.token").value("tok_abc"))
                .andExpect(jsonPath("$.panEnmascarado").value("************1111"))
                .andExpect(jsonPath("$.marca").value("VISA"))
                .andExpect(jsonPath("$.idTransaccion").value("corr-provided"))
                .andExpect(jsonPath("$.status").value("APROBADA"));

        assertThat(corrCap.getValue()).isEqualTo("corr-provided");
    }

    @Test
    void tokenize_shouldGenerateCorrelationId_whenHeaderMissing() throws Exception {
        var req = new TokenizacionRequest("4111111111111111", "123", "12/29", "John Doe");

        var res = okResponse("tok_xyz", "generated-corr");
        ArgumentCaptor<String> corrCap = ArgumentCaptor.forClass(String.class);
        when(adapter.execute(any(TokenizacionRequest.class), corrCap.capture())).thenReturn(res);

        mvc.perform(post("/api/v1/tokenize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                //.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/tokenize/tok_xyz"))
                .andExpect(jsonPath("$.token").value("tok_xyz"))
                .andExpect(jsonPath("$.status").value("APROBADA"));

        // El controller debe generar un UUID no vacío cuando no hay header
        assertThat(corrCap.getValue()).isNotBlank();
    }

    @Test
    void tokenize_shouldReturn400_onInvalidRequest_pan() throws Exception {
        var bad = new TokenizacionRequest("4111", "123", "12/29", "John Doe");

        mvc.perform(post("/api/v1/tokenize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                //.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void tokenize_shouldReturn400_onInvalidRequest_cvv_or_exp() throws Exception {
        var bad = new TokenizacionRequest("4111111111111111", "12", "13/29", "John Doe");

        mvc.perform(post("/api/v1/tokenize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                //.andDo(print())
                .andExpect(status().isBadRequest());
    }
}
