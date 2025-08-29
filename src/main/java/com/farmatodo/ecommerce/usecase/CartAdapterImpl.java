package com.farmatodo.ecommerce.usecase;

import com.farmatodo.ecommerce.DTOs.Request.AddCartItemRequest;
import com.farmatodo.ecommerce.DTOs.Request.AddToCartRequest;
import com.farmatodo.ecommerce.DTOs.Response.CartResponse;
import com.farmatodo.ecommerce.adapter.CartAdapter;
import com.farmatodo.ecommerce.usecase.UCCart;
import org.springframework.stereotype.Service;

@Service
public class CartAdapterImpl implements CartAdapter {
    private final UCCart uc;
    public CartAdapterImpl(UCCart uc){ this.uc = uc; }

    @Override public void addItem(Long customerId, AddCartItemRequest req){ uc.addItem(customerId, req); }
    @Override public CartResponse getCart(Long customerId){ return uc.getCart(customerId); }
    @Override public void removeItem(Long customerId, Long productId){ uc.removeItem(customerId, productId); }
    @Override public void clear(Long customerId){ uc.clear(customerId); }
}
