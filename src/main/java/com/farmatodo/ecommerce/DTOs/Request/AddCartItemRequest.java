package com.farmatodo.ecommerce.DTOs.Request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddCartItemRequest(
        @NotNull Long productId,
        @Min(1) Integer quantity
) {}
