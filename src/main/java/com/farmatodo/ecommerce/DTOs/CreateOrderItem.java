package com.farmatodo.ecommerce.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateOrderItem(@NotNull Long productId, @Min(1) Integer quantity) {}
