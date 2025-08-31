package com.farmatodo.ecommerce.usecase;

import com.farmatodo.ecommerce.DTOs.Request.CreateCustomerRequest;
import com.farmatodo.ecommerce.DTOs.Request.UpdateCustomerRequest;
import com.farmatodo.ecommerce.DTOs.Response.CustomerResponse;
import com.farmatodo.ecommerce.adapter.CustomerAdapter;
import com.farmatodo.ecommerce.entity.CustomerEntity;
import com.farmatodo.ecommerce.usecase.impl.CustomerImp;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class UCCustomer implements CustomerAdapter {

    private final CustomerImp useCase;

    public UCCustomer(CustomerImp useCase) {
        this.useCase = useCase;
    }

    @Override
    public CustomerResponse create(CreateCustomerRequest request) {
        CustomerEntity saved = useCase.create(CustomerMapper.toEntity(request));
        return CustomerMapper.toResponse(saved);
    }

    @Override
    public CustomerResponse get(Long id) {
        return CustomerMapper.toResponse(useCase.get(id));
    }


    @Override
    public Page<CustomerResponse> list(int page, int size) {
        return useCase.list(page, size).map(CustomerMapper::toResponse);
    }

    @Override
    public CustomerResponse update(Long id, UpdateCustomerRequest request) {
        var updated = useCase.update(
                id,
                CustomerMapper.toEntity(new CreateCustomerRequest(
                        request.name(), request.email(), request.phone(), request.address()))
        );
        return CustomerMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        useCase.delete(id);
    }
}
