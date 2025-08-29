package com.farmatodo.ecommerce.adapter;

import com.farmatodo.ecommerce.DTOs.Request.CreateCustomerRequest;
import com.farmatodo.ecommerce.DTOs.Request.UpdateCustomerRequest;
import com.farmatodo.ecommerce.DTOs.Response.CustomerResponse;
import org.springframework.data.domain.Page;


public interface CustomerAdapter {
    CustomerResponse create(CreateCustomerRequest request);
    CustomerResponse get(Long id);
    Page<CustomerResponse> list(int page, int size);
    CustomerResponse update(Long id, UpdateCustomerRequest request);
    void delete(Long id);
}
