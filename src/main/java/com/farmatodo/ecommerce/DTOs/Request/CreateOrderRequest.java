package com.farmatodo.ecommerce.DTOs.Request;

import com.farmatodo.ecommerce.DTOs.CreateOrderItem;
import jakarta.validation.constraints.*;
import java.util.List;

public record CreateOrderRequest(
        @NotNull Long customerId,
        @NotBlank String tokenizedCard,      // token de /tokenize
        @NotBlank String shippingAddress,
        @NotEmpty List<CreateOrderItem> items
) {}
