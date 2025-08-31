package com.farmatodo.ecommerce.controller;

import com.farmatodo.ecommerce.DTOs.Request.CreateProductRequest;
import com.farmatodo.ecommerce.DTOs.Response.ProductResponse;
import com.farmatodo.ecommerce.DTOs.Response.UpdateProductRequest;
import com.farmatodo.ecommerce.adapter.ProductAdapter;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductsController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductsControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductAdapter adapter;

    private ProductResponse prod(long id) {
        return new ProductResponse(
                id,
                "SKU-" + id,
                "Product " + id,
                "Description " + id,
                new BigDecimal("19.99"),
                10,
                "ACTIVE"
        );
    }

    @Test
    void create_shouldReturn201_withLocationAndBody() throws Exception {
        var req = new CreateProductRequest(
                "Acetaminofén 500mg",
                "Caja x 24",
                "Prueba descripcion",
                new BigDecimal("12.50"),
                50,
                2
        );

        var created = new ProductResponse(
                100L,
                req.sku(),
                req.name(),
                req.description(),
                req.price(),
                req.stock(),
                "ACTIVE"
        );

        when(adapter.create(any(CreateProductRequest.class))).thenReturn(created);

        mvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/products/100"))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.sku").value("Acetaminofén 500mg"))
                .andExpect(jsonPath("$.name").value("Caja x 24"))
                .andExpect(jsonPath("$.description").value("Prueba descripcion"))
                .andExpect(jsonPath("$.price").value(12.50))
                .andExpect(jsonPath("$.stock").value(50))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void get_shouldReturn200_withBody() throws Exception {
        when(adapter.get(5L)).thenReturn(prod(5L));

        mvc.perform(get("/api/v1/products/{id}", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.sku").value("SKU-5"))
                .andExpect(jsonPath("$.name").value("Product 5"));
    }

    @Test
    void list_shouldReturn200_withPageStructure() throws Exception {
        var page = new PageImpl<>(
                List.of(prod(1L), prod(2L)),
                PageRequest.of(0, 20),
                2
        );
        when(adapter.list(0, 20)).thenReturn(page);

        mvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].sku").value("SKU-1"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].sku").value("SKU-2"))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void update_shouldReturn200_withBody() throws Exception {
        var req = new UpdateProductRequest(
                "Producto 7 actualizado",
                "Desc",
                new BigDecimal("21.00"),
                99
        );

        var updated = new ProductResponse(
                7L,
                "SKU-7",
                req.name(),
                req.description(),
                req.price(),
                req.stock(),
                "ACTIVE"
        );
        when(adapter.update(eq(7L), any(UpdateProductRequest.class))).thenReturn(updated);

        mvc.perform(put("/api/v1/products/{id}", 7L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.sku").value("SKU-7"))
                .andExpect(jsonPath("$.name").value("Producto 7 actualizado"))
                .andExpect(jsonPath("$.description").value("Desc"))
                .andExpect(jsonPath("$.price").value(21.00))
                .andExpect(jsonPath("$.stock").value(99))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(adapter).update(eq(7L), any(UpdateProductRequest.class));
    }

    @Test
    void delete_shouldReturn204_andCallAdapter() throws Exception {
        mvc.perform(delete("/api/v1/products/{id}", 9L))
                .andExpect(status().isNoContent());
        verify(adapter).delete(9L);
    }

    @Test
    void search_shouldReturn200_withPageStructure() throws Exception {
        var page = new PageImpl<>(
                List.of(prod(11L), prod(12L)),
                PageRequest.of(0, 20),
                2
        );
        when(adapter.search(eq("vitamina"), eq(0), eq(20))).thenReturn(page);

        mvc.perform(get("/api/v1/products/search")
                        .param("q", "vitamina"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(11))
                .andExpect(jsonPath("$.content[1].id").value(12))
                .andExpect(jsonPath("$.totalElements").value(2));
    }
}
