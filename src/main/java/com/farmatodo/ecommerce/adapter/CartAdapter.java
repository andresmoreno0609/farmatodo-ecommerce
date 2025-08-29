package com.farmatodo.ecommerce.adapter;


import com.farmatodo.ecommerce.DTOs.Request.AddCartItemRequest;
import com.farmatodo.ecommerce.DTOs.Request.AddToCartRequest;
import com.farmatodo.ecommerce.DTOs.Response.CartResponse;

public interface CartAdapter {
    void addItem(Long customerId, AddCartItemRequest req);
    CartResponse getCart(Long customerId);
    void removeItem(Long customerId, Long productId);
    void clear(Long customerId);
}
