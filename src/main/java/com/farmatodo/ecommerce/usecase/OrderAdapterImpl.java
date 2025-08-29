package com.farmatodo.ecommerce.usecase;


import com.farmatodo.ecommerce.DTOs.Request.CreateOrderRequest;
import com.farmatodo.ecommerce.DTOs.Response.OrderResponse;
import com.farmatodo.ecommerce.adapter.OrderAdapter;
import com.farmatodo.ecommerce.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderAdapterImpl implements OrderAdapter {

    private final UCOrder uc;
    public OrderAdapterImpl(UCOrder uc){ this.uc = uc; }

    private static OrderResponse toResponse(OrderEntity o){
        var items = o.getItems().stream().map(i ->
                new OrderResponse.Item(i.getProductId(), i.getProductSku(), i.getProductName(),
                        i.getQuantity(), i.getUnitPrice(),
                        i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity()))
                )).toList();
        var pay = o.getPayment()==null ? null :
                new OrderResponse.Payment(o.getPayment().getId(), o.getPayment().getStatus().name(),
                        o.getPayment().getAttempts(), o.getPayment().getLastError());
        return new OrderResponse(o.getId(), o.getCustomerId(), o.getStatus().name(),
                o.getTotal(), items, pay, o.getCreatedAt());
    }

    @Override public OrderResponse create(CreateOrderRequest req){ return toResponse(uc.create(req)); }
    @Override public OrderResponse get(Long id){ return toResponse(uc.get(id)); }
    @Override public Page<OrderResponse> list(int page, int size){ return uc.list(page, size).map(OrderAdapterImpl::toResponse); }

}
