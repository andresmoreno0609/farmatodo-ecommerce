package com.farmatodo.ecommerce.usecase;

import com.farmatodo.ecommerce.DTOs.Request.CreateCustomerRequest;
import com.farmatodo.ecommerce.DTOs.Request.UpdateCustomerRequest;
import com.farmatodo.ecommerce.DTOs.Response.CustomerResponse;
import com.farmatodo.ecommerce.entity.CustomerEntity;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.*;

class CustomerMapperTest {

    @Test
    void toEntity_shouldMapAllFields() {
        var req = new CreateCustomerRequest(
                "Andrés",
                "andres@example.com",
                "+57 3000000000",
                "Calle 123 #45-67"
        );

        CustomerEntity e = CustomerMapper.toEntity(req);

        assertThat(e).isNotNull();
        assertThat(e.getName()).isEqualTo("Andrés");
        assertThat(e.getEmail()).isEqualTo("andres@example.com");
        assertThat(e.getPhone()).isEqualTo("+57 3000000000");
        assertThat(e.getAddress()).isEqualTo("Calle 123 #45-67");
        assertThat(e.getId()).isNull();
    }

    @Test
    void merge_shouldOverrideAllMutableFields() {
        var existing = new CustomerEntity();
        existing.setId(99L);
        existing.setName("Viejo");
        existing.setEmail("viejo@example.com");
        existing.setPhone("111");
        existing.setAddress("Antigua 1");

        // y un request con datos nuevos
        var req = new UpdateCustomerRequest(
                "Nuevo",
                "nuevo@example.com",
                "222",
                "Nueva 2"
        );

        // when
        CustomerMapper.merge(existing, req);

        assertThat(existing.getId()).isEqualTo(99L);
        assertThat(existing.getName()).isEqualTo("Nuevo");
        assertThat(existing.getEmail()).isEqualTo("nuevo@example.com");
        assertThat(existing.getPhone()).isEqualTo("222");
        assertThat(existing.getAddress()).isEqualTo("Nueva 2");
    }

    @Test
    void toResponse_shouldMapAllFields_andReturnNullStatusWhenEntityStatusIsNull() {
        // given
        var e = new CustomerEntity();
        e.setId(7L);
        e.setName("Ana");
        e.setEmail("ana@example.com");
        e.setPhone("555");
        e.setAddress("Calle 7");

        // when
        CustomerResponse res = CustomerMapper.toResponse(e);

        // then
        assertThat(res).isNotNull();
        assertThat(res.id()).isEqualTo(7L);
        assertThat(res.name()).isEqualTo("Ana");
        assertThat(res.email()).isEqualTo("ana@example.com");
        assertThat(res.phone()).isEqualTo("555");
        assertThat(res.address()).isEqualTo("Calle 7");
        assertThat(res.status()).isNull();
    }

    @Test
    void constructor_shouldBePrivate_andReachableOnlyViaReflection_forCoverage() throws Exception {
        Constructor<CustomerMapper> ctor = CustomerMapper.class.getDeclaredConstructor();
        assertThat(ctor.canAccess(null)).isFalse();
        ctor.setAccessible(true);

        var instance = ctor.newInstance();
        assertThat(instance).isNotNull();
    }
}
