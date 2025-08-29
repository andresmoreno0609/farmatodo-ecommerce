package com.farmatodo.ecommerce.controller;

import com.farmatodo.ecommerce.DTOs.Request.AddCartItemRequest;
import com.farmatodo.ecommerce.DTOs.Response.CartResponse;
import com.farmatodo.ecommerce.adapter.CartAdapter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Cart")
@RestController
@RequestMapping("/api/v1/carts")
@SecurityRequirement(name = "apiKeyAuth")
public class CartController {
    private final CartAdapter adapter;
    public CartController(CartAdapter adapter){ this.adapter = adapter; }

    @PostMapping("/{customerId}/items")
    public ResponseEntity<Void> addItem(@PathVariable Long customerId,
                                        @Valid @org.springframework.web.bind.annotation.RequestBody AddCartItemRequest body) {
        adapter.addItem(customerId, body);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable Long customerId) {
        var cart = adapter.getCart(customerId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/{customerId}/items/{productId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long customerId, @PathVariable Long productId) {
        adapter.removeItem(customerId, productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> clear(@PathVariable Long customerId) {
        adapter.clear(customerId);
        return ResponseEntity.noContent().build();
    }
}
