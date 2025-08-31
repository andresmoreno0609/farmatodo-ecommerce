package com.farmatodo.ecommerce.usecase.Impl;

import com.farmatodo.ecommerce.entity.CustomerEntity;
import com.farmatodo.ecommerce.enums.ERecordStatus;
import com.farmatodo.ecommerce.exceptions.BusinessException;
import com.farmatodo.ecommerce.exceptions.NotFoundException;
import com.farmatodo.ecommerce.repository.CustomerRepository;
import com.farmatodo.ecommerce.usecase.impl.CustomerImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerImpTest {

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerImp useCase;

    private CustomerEntity newCustomer;
    private CustomerEntity existingCustomer;

    @BeforeEach
    void setUp() {
        newCustomer = customer(null, "Alice", "alice@demo.com", "3001112233", "Street 1", null);
        existingCustomer = customer(1L, "Bob", "bob@demo.com", "3009998877", "Street 9", ERecordStatus.ACTIVE);
    }

    @Test
    void create_ok_setsActive_andSaves() {
        when(repository.existsByEmail(newCustomer.getEmail())).thenReturn(false);
        when(repository.existsByPhone(newCustomer.getPhone())).thenReturn(false);
        when(repository.save(any(CustomerEntity.class)))
                .thenAnswer(inv -> { CustomerEntity c = inv.getArgument(0); c.setId(10L); return c; });

        CustomerEntity saved = useCase.create(newCustomer);

        assertNotNull(saved.getId());
        assertEquals(ERecordStatus.ACTIVE, saved.getStatus());
        verify(repository).save(any(CustomerEntity.class));
    }

    @Test
    void create_fail_emailExists_throwsBusiness() {
        when(repository.existsByEmail(newCustomer.getEmail())).thenReturn(true);

        assertThrows(BusinessException.class, () -> useCase.create(newCustomer));
        verify(repository, never()).save(any());
    }

    @Test
    void create_fail_phoneExists_throwsBusiness() {
        when(repository.existsByEmail(newCustomer.getEmail())).thenReturn(false);
        when(repository.existsByPhone(newCustomer.getPhone())).thenReturn(true);

        assertThrows(BusinessException.class, () -> useCase.create(newCustomer));
        verify(repository, never()).save(any());
    }

    @Test
    void get_ok_returnsEntity() {
        when(repository.findById(1L)).thenReturn(Optional.of(existingCustomer));

        CustomerEntity got = useCase.get(1L);

        assertEquals(1L, got.getId());
        assertEquals("Bob", got.getName());
    }

    @Test
    void get_notFound_throws() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> useCase.get(99L));
    }

    @Test
    void list_ok_returnsPage() {
        PageRequest pr = PageRequest.of(0, 2);
        when(repository.findAll(pr))
                .thenReturn(new PageImpl<>(List.of(existingCustomer), pr, 1));

        Page<CustomerEntity> page = useCase.list(0, 2);

        assertEquals(1, page.getTotalElements());
        assertEquals(1, page.getContent().size());
        assertEquals(existingCustomer.getId(), page.getContent().get(0).getId());
    }

    @Test
    void update_ok_changesFields_andSaves_whenUniqUnchanged() {
        when(repository.findById(1L)).thenReturn(Optional.of(existingCustomer));
        when(repository.save(any(CustomerEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        CustomerEntity patch = customer(
                null,
                "Robert",
                existingCustomer.getEmail(),
                existingCustomer.getPhone(),
                "New Addr",
                null
        );

        CustomerEntity updated = useCase.update(1L, patch);

        assertEquals("Robert", updated.getName());
        assertEquals("New Addr", updated.getAddress());
        verify(repository).save(any(CustomerEntity.class));
    }

    @Test
    void update_fail_emailAlreadyUsed_throwsBusiness() {
        when(repository.findById(1L)).thenReturn(Optional.of(existingCustomer));
        when(repository.existsByEmailAndIdNot("new@mail.com", 1L)).thenReturn(true);

        CustomerEntity patch = customer(
                null,
                "Bob",
                "new@mail.com",
                existingCustomer.getPhone(),
                existingCustomer.getAddress(),
                null
        );

        assertThrows(BusinessException.class, () -> useCase.update(1L, patch));
        verify(repository, never()).save(any());
    }

    @Test
    void update_fail_phoneAlreadyUsed_throwsBusiness() {
        when(repository.findById(1L)).thenReturn(Optional.of(existingCustomer));
        when(repository.existsByPhoneAndIdNot("3000000000", 1L)).thenReturn(true);

        CustomerEntity patch = customer(
                null,
                "Bob",
                existingCustomer.getEmail(),
                "3000000000",
                existingCustomer.getAddress(),
                null
        );

        assertThrows(BusinessException.class, () -> useCase.update(1L, patch));
        verify(repository, never()).save(any());
    }

    @Test
    void update_notFound_throws() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> useCase.update(99L, newCustomer));
    }

    @Test
    void delete_ok_marksDeleted_andSaves() {
        when(repository.findById(1L)).thenReturn(Optional.of(existingCustomer));

        useCase.delete(1L);

        assertEquals(ERecordStatus.DELETED, existingCustomer.getStatus());
        verify(repository).save(existingCustomer);
    }

    @Test
    void delete_notFound_throws() {
        when(repository.findById(77L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> useCase.delete(77L));
        verify(repository, never()).save(any());
    }

    private static CustomerEntity customer(Long id, String name, String email, String phone, String address, ERecordStatus status) {
        CustomerEntity c = new CustomerEntity();
        c.setId(id);
        c.setName(name);
        c.setEmail(email);
        c.setPhone(phone);
        c.setAddress(address);
        c.setStatus(status);
        return c;
    }
}
