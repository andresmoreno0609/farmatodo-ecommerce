package com.farmatodo.ecommerce.adapter;

import com.farmatodo.ecommerce.DTOs.Request.CreateOrderRequest;
import com.farmatodo.ecommerce.DTOs.Response.OrderResponse;
import org.springframework.data.domain.Page;

public interface OrderAdapter {
    OrderResponse create(CreateOrderRequest req);
    OrderResponse get(Long id);
    Page<OrderResponse> list(int page, int size);
}
