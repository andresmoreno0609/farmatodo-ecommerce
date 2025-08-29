package com.farmatodo.ecommerce.DTOs.Response;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(Long cartId, Long customerId, List<CartItemResponse> items, BigDecimal total) {}
