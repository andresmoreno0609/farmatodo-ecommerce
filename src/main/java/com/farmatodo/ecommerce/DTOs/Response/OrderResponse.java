package com.farmatodo.ecommerce.DTOs.Response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OrderResponse(
        Long id, Long customerId, String status, BigDecimal total,
        List<Item> items, Payment payment, OffsetDateTime createdAt
){
    public record Item(Long productId, String sku, String name, Integer quantity, BigDecimal unitPrice, BigDecimal lineTotal){}
    public record Payment(Long id, String status, Integer attempts, String lastError){}
}