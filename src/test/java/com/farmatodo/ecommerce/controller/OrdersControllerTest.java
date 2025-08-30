package com.farmatodo.ecommerce.controller;

import com.farmatodo.ecommerce.DTOs.CreateOrderItem;
import com.farmatodo.ecommerce.DTOs.Request.CreateOrderRequest;
import com.farmatodo.ecommerce.adapter.OrderAdapter;
import com.farmatodo.ecommerce.DTOs.Response.OrderResponse;
import com.farmatodo.ecommerce.DTOs.Response.OrderResponse.Item;
import com.farmatodo.ecommerce.DTOs.Response.OrderResponse.Payment;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrdersController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrdersControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    OrderAdapter adapter;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderResponse order(long id, long customerId) {
        return new OrderResponse(
                id,
                customerId,
                "PENDING",
                new BigDecimal("59.98"),
                List.of(
                        new Item(10L, "SKU-10", "Producto 10", 1, new BigDecimal("19.99"), new BigDecimal("19.99")),
                        new Item(11L, "SKU-11", "Producto 11", 2, new BigDecimal("20.00"), new BigDecimal("40.00"))
                ),
                new Payment(77L, "INIT", 0, null),
                OffsetDateTime.parse("2025-08-30T12:00:00Z")
        );
    }

    @Test
    void create_shouldReturn201_withLocationAndBody() throws Exception {
        var created = order(123L, 5L);

        when(adapter.create(any())).thenReturn(created);

        var req = new CreateOrderRequest(
                5L,
                "tok_abc123",
                "Calle 123 #45-67",
                List.of(
                        new CreateOrderItem(10L, 1),
                        new CreateOrderItem(11L, 2)
                )
        );

        String json = objectMapper.writeValueAsString(req);

        mvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/orders/123"))
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.customerId").value(5))
                .andExpect(jsonPath("$.items[0].productId").value(10))
                .andExpect(jsonPath("$.items[1].quantity").value(2));
    }

    @Test
    void get_shouldReturn200_withBody() throws Exception {
        when(adapter.get(9L)).thenReturn(order(9L, 3L));

        mvc.perform(get("/api/v1/orders/{id}", 9L))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(9))
                .andExpect(jsonPath("$.customerId").value(3))
                .andExpect(jsonPath("$.items[0].sku").value("SKU-10"))
                .andExpect(jsonPath("$.payment.id").value(77));
    }

    @Test
    void list_shouldReturn200_withPageStructure() throws Exception {
        var page = new PageImpl<>(
                List.of(order(1L, 1L), order(2L, 1L)),
                PageRequest.of(0, 20),
                2
        );
        when(adapter.list(0, 20)).thenReturn(page);

        mvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(2));
    }
}

