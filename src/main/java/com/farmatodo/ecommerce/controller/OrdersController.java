package com.farmatodo.ecommerce.controller;

import com.farmatodo.ecommerce.DTOs.Request.CreateOrderRequest;
import com.farmatodo.ecommerce.DTOs.Response.OrderResponse;
import com.farmatodo.ecommerce.adapter.OrderAdapter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Orders")
@RestController
@RequestMapping("/api/v1/orders")
@SecurityRequirement(name = "apiKeyAuth")
public class OrdersController {

    private final OrderAdapter adapter;
    public OrdersController(OrderAdapter adapter){ this.adapter = adapter; }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @org.springframework.web.bind.annotation.RequestBody CreateOrderRequest r){
        var body = adapter.create(r);
        return ResponseEntity.created(URI.create("/api/v1/orders/"+body.id())).body(body);
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable Long id){ return adapter.get(id); }

    @GetMapping
    public Page<OrderResponse> list(@RequestParam(defaultValue="0") int page,
                                    @RequestParam(defaultValue="20") int size){
        return adapter.list(page, size);
    }
}
