package com.farmatodo.ecommerce.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payments")
@RestController
@RequestMapping("/api/v1/payments")
@SecurityRequirement(name = "apiKeyAuth")
public class PaymentsController {
    @PostMapping("/{orderId}/approve")
    public ResponseEntity<Void> approve(@PathVariable Long orderId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/{orderId}/status")
    public ResponseEntity<Void> status(@PathVariable Long orderId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
