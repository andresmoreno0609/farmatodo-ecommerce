package com.farmatodo.ecommerce.usecase;

import com.farmatodo.ecommerce.DTOs.Request.CreateCustomerRequest;
import com.farmatodo.ecommerce.DTOs.Request.UpdateCustomerRequest;
import com.farmatodo.ecommerce.DTOs.Response.CustomerResponse;
import com.farmatodo.ecommerce.entity.CustomerEntity;

public final class CustomerMapper {
    private CustomerMapper(){}

    public static CustomerEntity toEntity(CreateCustomerRequest r){
        var e = new CustomerEntity();
        e.setName(r.name());
        e.setEmail(r.email());
        e.setPhone(r.phone());
        e.setAddress(r.address());
        return e;
    }
    public static void merge(CustomerEntity e, UpdateCustomerRequest r){
        e.setName(r.name());
        e.setEmail(r.email());
        e.setPhone(r.phone());
        e.setAddress(r.address());
    }
    public static CustomerResponse toResponse(CustomerEntity e){
        return new CustomerResponse(
                e.getId(), e.getName(), e.getEmail(), e.getPhone(), e.getAddress(),
                e.getStatus()!=null?e.getStatus().name():null
        );
    }
}
