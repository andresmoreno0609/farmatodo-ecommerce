package com.farmatodo.ecommerce.usecase;

import com.farmatodo.ecommerce.DTOs.Request.CreateCustomerRequest;
import com.farmatodo.ecommerce.DTOs.Request.UpdateCustomerRequest;
import com.farmatodo.ecommerce.DTOs.Response.CustomerResponse;
import com.farmatodo.ecommerce.entity.CustomerEntity;
import com.farmatodo.ecommerce.usecase.impl.CustomerImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UCCustomerTest {

    @Mock
    private CustomerImp useCase;

    @InjectMocks
    private UCCustomer uc;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    private CustomerEntity entity(Long id, String name, String email, String phone, String address) {
        var e = new CustomerEntity();
        e.setId(id);
        e.setName(name);
        e.setEmail(email);
        e.setPhone(phone);
        e.setAddress(address);
        return e;
    }

    @Test
    void create_shouldMapRequest_toEntity_andReturnResponse() {
        var req = new CreateCustomerRequest(
                "Andrés",
                "andres@example.com",
                "+57 3000000000",
                "Calle 123 #45-67"
        );

        var saved = entity(10L, "Andrés", "andres@example.com", "+57 3000000000", "Calle 123 #45-67");
        when(useCase.create(any(CustomerEntity.class))).thenReturn(saved);

        ArgumentCaptor<CustomerEntity> captor = ArgumentCaptor.forClass(CustomerEntity.class);

        CustomerResponse res = uc.create(req);

        verify(useCase).create(captor.capture());
        var passed = captor.getValue();
        assertThat(passed.getId()).isNull(); // antes de persistir no hay id
        assertThat(passed.getName()).isEqualTo("Andrés");
        assertThat(passed.getEmail()).isEqualTo("andres@example.com");
        assertThat(passed.getPhone()).isEqualTo("+57 3000000000");
        assertThat(passed.getAddress()).isEqualTo("Calle 123 #45-67");

        assertThat(res.id()).isEqualTo(10L);
        assertThat(res.name()).isEqualTo("Andrés");
        assertThat(res.email()).isEqualTo("andres@example.com");
        assertThat(res.phone()).isEqualTo("+57 3000000000");
        assertThat(res.address()).isEqualTo("Calle 123 #45-67");
        assertThat(res.status()).isNull(); // mapper pone null si entidad no tiene status
    }

    @Test
    void get_shouldReturnMappedResponse() {
        when(useCase.get(7L)).thenReturn(entity(7L, "Ana", "ana@example.com", "555", "Calle 7"));

        CustomerResponse res = uc.get(7L);

        verify(useCase).get(7L);
        assertThat(res.id()).isEqualTo(7L);
        assertThat(res.name()).isEqualTo("Ana");
        assertThat(res.email()).isEqualTo("ana@example.com");
        assertThat(res.phone()).isEqualTo("555");
        assertThat(res.address()).isEqualTo("Calle 7");
        assertThat(res.status()).isNull();
    }

    @Test
    void list_shouldMapPageEntities_toPageResponses() {
        var e1 = entity(1L, "Uno", "uno@example.com", "111", "Dir 1");
        var e2 = entity(2L, "Dos", "dos@example.com", "222", "Dir 2");
        Page<CustomerEntity> page = new PageImpl<>(List.of(e1, e2), PageRequest.of(0, 2), 2);
        when(useCase.list(0, 2)).thenReturn(page);

        Page<CustomerResponse> res = uc.list(0, 2);

        verify(useCase).list(0, 2);
        assertThat(res.getTotalElements()).isEqualTo(2);
        assertThat(res.getContent()).hasSize(2);
        assertThat(res.getContent().get(0).id()).isEqualTo(1L);
        assertThat(res.getContent().get(0).name()).isEqualTo("Uno");
        assertThat(res.getContent().get(1).email()).isEqualTo("dos@example.com");
    }

    @Test
    void update_shouldBuildEntityFromUpdateRequest_andReturnMappedResponse() {
        Long id = 15L;
        var req = new UpdateCustomerRequest(
                "Nuevo",
                "nuevo@example.com",
                "999",
                "Nueva 9"
        );

        var updated = entity(id, "Nuevo", "nuevo@example.com", "999", "Nueva 9");
        when(useCase.update(eq(id), any(CustomerEntity.class))).thenReturn(updated);

        ArgumentCaptor<CustomerEntity> captor = ArgumentCaptor.forClass(CustomerEntity.class);

        CustomerResponse res = uc.update(id, req);

        verify(useCase).update(eq(id), captor.capture());
        var sent = captor.getValue();
        assertThat(sent.getId()).isNull();
        assertThat(sent.getName()).isEqualTo("Nuevo");
        assertThat(sent.getEmail()).isEqualTo("nuevo@example.com");
        assertThat(sent.getPhone()).isEqualTo("999");
        assertThat(sent.getAddress()).isEqualTo("Nueva 9");

        assertThat(res.id()).isEqualTo(15L);
        assertThat(res.name()).isEqualTo("Nuevo");
        assertThat(res.email()).isEqualTo("nuevo@example.com");
    }

    @Test
    void delete_shouldDelegateToUseCase() {
        uc.delete(33L);
        verify(useCase).delete(33L);
    }
}
