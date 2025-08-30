package com.farmatodo.ecommerce.DTOs.Request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank @Size(max=80)  String sku,
        @NotBlank @Size(max=160) String name,
        @Size(max=500)           String description,
        @NotNull @DecimalMin("0.0") BigDecimal price,
        @NotNull @Min(0) Integer stock,
        int i) {}