package com.farmatodo.ecommerce.controller;

import com.farmatodo.ecommerce.adapter.CartAdapter;
import com.farmatodo.ecommerce.DTOs.Request.AddCartItemRequest;
import com.farmatodo.ecommerce.DTOs.Response.CartResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CartController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean CartAdapter adapter;

    @Test
    void addItem_shouldReturn204() throws Exception {
        var req = new AddCartItemRequest(10L, 2);

        mvc.perform(post("/api/v1/carts/{customerId}/items", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                //.andDo(print())
                .andExpect(status().isNoContent());

        verify(adapter).addItem(eq(99L), any(AddCartItemRequest.class));
    }

    @Test
    void getCart_shouldReturn200_withCartBody() throws Exception {
        var response = new CartResponse(1L, 99L, List.of(), new BigDecimal("0.00"));
        when(adapter.getCart(99L)).thenReturn(response);

        mvc.perform(get("/api/v1/carts/{customerId}", 99L))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(1))
                .andExpect(jsonPath("$.customerId").value(99))
                .andExpect(jsonPath("$.total").value(0.00))
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    void removeItem_shouldReturn200() throws Exception {
        mvc.perform(delete("/api/v1/carts/{customerId}/items/{productId}", 99L, 10L))
                //.andDo(print())
                .andExpect(status().isOk());

        verify(adapter).removeItem(99L, 10L);
    }

    @Test
    void clear_shouldReturn204() throws Exception {
        mvc.perform(delete("/api/v1/carts/{customerId}", 99L))
                //.andDo(print())
                .andExpect(status().isNoContent());

        verify(adapter).clear(99L);
    }
}
