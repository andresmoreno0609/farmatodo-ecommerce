package com.farmatodo.ecommerce.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = HealthController.class)
@AutoConfigureMockMvc(addFilters = false) // desactiva filtros de seguridad para este slice
class HealthControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void ping_shouldReturn200_withPongBody() throws Exception {
        mvc.perform(get("/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("pong"));
        // Si quisieras validar content-type:
        // .andExpect(content().contentTypeCompatibleWith("text/plain"));
    }
}
