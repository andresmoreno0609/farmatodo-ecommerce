package com.farmatodo.ecommerce.usecase;

import com.farmatodo.ecommerce.DTOs.Request.CreateOrderRequest;
import com.farmatodo.ecommerce.DTOs.*;
import com.farmatodo.ecommerce.entity.*;
import com.farmatodo.ecommerce.enums.*;
import com.farmatodo.ecommerce.exceptions.BusinessException;
import com.farmatodo.ecommerce.exceptions.NotFoundException;
import com.farmatodo.ecommerce.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class UCOrder {

    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;

    public UCOrder(OrderRepository orderRepo, ProductRepository productRepo) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
    }

    @Transactional
    public OrderEntity create(CreateOrderRequest r) {
        if (r.items() == null || r.items().isEmpty()) throw new BusinessException("empty_order");

        var order = new OrderEntity();
        order.setCustomerId(r.customerId());
        order.setShippingAddress(r.shippingAddress());
        order.setStatus(EOrderStatus.CREATED);

        BigDecimal total = BigDecimal.ZERO;
        for (var it : r.items()) {
            var product = productRepo.findById(it.productId())
                    .orElseThrow(() -> new NotFoundException("product_not_found"));

            if (product.getStock() < it.quantity())
                throw new BusinessException("insufficient_stock");

            // resta stock (reserva)
            product.setStock(product.getStock() - it.quantity());
            productRepo.save(product);

            var oi = new OrderItemEntity();
            oi.setOrder(order);
            oi.setProductId(product.getId());
            oi.setProductSku(product.getSku());
            oi.setProductName(product.getName());
            oi.setUnitPrice(product.getPrice());
            oi.setQuantity(it.quantity());
            order.getItems().add(oi);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(it.quantity())));
        }
        order.setTotal(total);

        // pago inicial en PENDING
        var pay = new PaymentEntity();
        pay.setOrder(order);
        pay.setTokenizedCard(r.tokenizedCard());
        pay.setStatus(EPaymentStatus.PENDING);
        pay.setAttempts(0);
        order.setPayment(pay);

        return orderRepo.save(order); // cascade guarda items y payment
    }

    @Transactional(readOnly = true)
    public OrderEntity get(Long id) {
        return orderRepo.findById(id).orElseThrow(() -> new NotFoundException("order_not_found"));
    }

    @Transactional(readOnly = true)
    public Page<OrderEntity> list(int page, int size) {
        return orderRepo.findAll(PageRequest.of(page, size));
    }

    @Transactional
    public void markPaid(OrderEntity order) {
        order.setStatus(EOrderStatus.PAID);
        orderRepo.save(order);
    }

    @Transactional
    public void markFailed(OrderEntity order) {
        order.setStatus(EOrderStatus.FAILED);
        orderRepo.save(order);
    }
}
