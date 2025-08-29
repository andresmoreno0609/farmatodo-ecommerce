package com.farmatodo.ecommerce.DTOs.Request;

import jakarta.validation.constraints.*;

public record UpdateCustomerRequest(
        @NotBlank @Size(min=2,max=120) String name,
        @NotBlank @Email String email,
        @NotBlank @Size(min=7,max=30) String phone,
        @Size(max=255) String address
) {}