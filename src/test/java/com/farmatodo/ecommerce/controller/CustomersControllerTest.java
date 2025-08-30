package com.farmatodo.ecommerce.controller;

import com.farmatodo.ecommerce.DTOs.Request.CreateCustomerRequest;
import com.farmatodo.ecommerce.DTOs.Request.UpdateCustomerRequest;
import com.farmatodo.ecommerce.DTOs.Response.CustomerResponse;
import com.farmatodo.ecommerce.adapter.CustomerAdapter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = CustomersController.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomersControllerTest {

    @Autowired MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean CustomerAdapter adapter;

    private CustomerResponse resp(long id) {
        return new CustomerResponse(
                id,
                "Andrés",
                "andres@example.com",
                "3001234567",
                "Calle 123 #45-67",
                "ACTIVE"
        );
    }

    @Test
    void create_shouldReturn201_withLocationAndBody() throws Exception {
        var created = resp(1L);
        when(adapter.create(any(CreateCustomerRequest.class))).thenReturn(created);

        var req = new CreateCustomerRequest(
                "Andrés",
                "andres@example.com",
                "3001234567",
                "Calle 123 #45-67"
        );

        mvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/customers/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Andrés"))
                .andExpect(jsonPath("$.email").value("andres@example.com"))
                .andExpect(jsonPath("$.phone").value("3001234567"))
                .andExpect(jsonPath("$.address").value("Calle 123 #45-67"));
    }

    @Test
    void get_shouldReturn200_withBody() throws Exception {
        when(adapter.get(1L)).thenReturn(resp(1L));

        mvc.perform(get("/api/v1/customers/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("andres@example.com"));
    }

    @Test
    void list_shouldReturn200_withPageStructure() throws Exception {
        var page = new PageImpl<>(
                List.of(resp(1L), resp(2L)),
                PageRequest.of(0, 20),
                2
        );
        when(adapter.list(0, 20)).thenReturn(page);

        mvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void update_shouldReturn200_withUpdatedBody() throws Exception {
        var updated = new CustomerResponse(
                1L, "Andrés Actualizado", "andres@example.com", "3009998888", "Nueva Dirección 123","ACTIVE"
        );
        when(adapter.update(eq(1L), any(UpdateCustomerRequest.class))).thenReturn(updated);

        var req = new UpdateCustomerRequest(
                "Andrés Actualizado",
                "andres@example.com",
                "3009998888",
                "Nueva Dirección 123"
        );

        mvc.perform(put("/api/v1/customers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Andrés Actualizado"))
                .andExpect(jsonPath("$.phone").value("3009998888"))
                .andExpect(jsonPath("$.address").value("Nueva Dirección 123"));
    }

    @Test
    void delete_shouldReturn204_andCallAdapter() throws Exception {
        mvc.perform(delete("/api/v1/customers/{id}", 1L))
                .andExpect(status().isNoContent());
        Mockito.verify(adapter).delete(1L);
    }
}
