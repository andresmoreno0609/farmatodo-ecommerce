package com.farmatodo.ecommerce.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Orders")
@RestController
@RequestMapping("/api/v1/orders")
@SecurityRequirement(name = "apiKeyAuth")
public class OrdersController {
    @PostMapping
    public ResponseEntity<Void> create() { return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }

    @GetMapping("/{id}")
    public ResponseEntity<Void> get(@PathVariable Long id) { return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }

    @GetMapping
    public ResponseEntity<Void> list() { return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }
}
