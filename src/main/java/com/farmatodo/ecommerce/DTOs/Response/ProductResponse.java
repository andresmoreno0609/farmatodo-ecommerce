package com.farmatodo.ecommerce.DTOs.Response;

import java.math.BigDecimal;

public record ProductResponse(
        Long id, String sku, String name, String description,
        BigDecimal price, Integer stock, String status
) {}
