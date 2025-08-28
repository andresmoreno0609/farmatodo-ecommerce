package com.farmatodo.ecommerce.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Cart")
@RestController
@RequestMapping("/api/v1/carts")
@SecurityRequirement(name = "apiKeyAuth")
public class CartController {
    @PostMapping("/{customerId}/items")
    public ResponseEntity<Void> addItem(@PathVariable Long customerId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Void> getCart(@PathVariable Long customerId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/{customerId}/items/{productId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long customerId, @PathVariable Long productId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> clear(@PathVariable Long customerId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
