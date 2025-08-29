package com.farmatodo.ecommerce.DTOs.Response;

public record CustomerResponse(
        Long id, String name, String email, String phone, String address, String status
) {}