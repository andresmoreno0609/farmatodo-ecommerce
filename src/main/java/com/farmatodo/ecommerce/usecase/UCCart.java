package com.farmatodo.ecommerce.usecase;


import com.farmatodo.ecommerce.DTOs.Request.AddCartItemRequest;
import com.farmatodo.ecommerce.DTOs.Response.CartItemResponse;
import com.farmatodo.ecommerce.DTOs.Response.CartResponse;
import com.farmatodo.ecommerce.entity.*;
import com.farmatodo.ecommerce.exceptions.NotFoundException;
import com.farmatodo.ecommerce.repository.CartItemRepository;
import com.farmatodo.ecommerce.repository.CartRepository;
import com.farmatodo.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UCCart {

    private final CartRepository cartRepo;
    private final CartItemRepository itemRepo;
    private final ProductRepository productRepo;

    public UCCart(CartRepository cartRepo, CartItemRepository itemRepo, ProductRepository productRepo) {
        this.cartRepo = cartRepo;
        this.itemRepo = itemRepo;
        this.productRepo = productRepo;
    }

    @Transactional
    public void addItem(Long customerId, AddCartItemRequest req){
        var cart = cartRepo.findByCustomerId(customerId)
                .orElseGet(() -> cartRepo.save(newCart(customerId)));

        var product = productRepo.findById(req.productId())
                .orElseThrow(() -> new NotFoundException("product_not_found"));

        var item = itemRepo.findByCartIdAndProductId(cart.getId(), req.productId())
                .orElseGet(() -> {
                    var it = new CartItemEntity();
                    it.setCart(cart);
                    it.setProductId(product.getId());
                    it.setProductName(product.getName());
                    it.setProductSku(product.getSku());
                    it.setUnitPrice(product.getPrice());
                    it.setQuantity(0);
                    return it;
                });

        item.setQuantity(item.getQuantity() + req.quantity());
        itemRepo.save(item);
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(Long customerId){
        var cartOpt = cartRepo.findWithItemsByCustomerId(customerId);
        if (cartOpt.isEmpty()) {
            // carrito vacío “virtual”
            return new CartResponse(null, customerId, List.of(), BigDecimal.ZERO);
        }
        var cart = cartOpt.get();
        var items = cart.getItems().stream().map(it -> {
            var line = it.getUnitPrice().multiply(BigDecimal.valueOf(it.getQuantity()));
            return new CartItemResponse(it.getProductId(), it.getProductSku(), it.getProductName(),
                    it.getQuantity(), it.getUnitPrice(), line);
        }).toList();
        var total = items.stream().map(CartItemResponse::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(cart.getId(), customerId, items, total);
    }

    @Transactional
    public void removeItem(Long customerId, Long productId){
        var cart = cartRepo.findByCustomerId(customerId)
                .orElseThrow(() -> new NotFoundException("cart_not_found"));

        // idempotente: si no existe el ítem, no falla
        itemRepo.deleteByCartIdAndProductId(cart.getId(), productId);
    }

    @Transactional
    public void clear(Long customerId){
        var cart = cartRepo.findByCustomerId(customerId)
                .orElseThrow(() -> new NotFoundException("cart_not_found"));

        itemRepo.deleteByCartId(cart.getId());
    }

    private CartEntity newCart(Long customerId){
        var c = new CartEntity();
        c.setCustomerId(customerId);
        return c;
    }
}
