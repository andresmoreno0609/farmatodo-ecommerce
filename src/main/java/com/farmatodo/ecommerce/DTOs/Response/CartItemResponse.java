package com.farmatodo.ecommerce.DTOs.Response;

import java.math.BigDecimal;

public record CartItemResponse(
        Long productId, String sku, String name, Integer quantity, BigDecimal unitPrice, BigDecimal lineTotal
) {}
