package com.farmatodo.ecommerce.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Customers")
@RestController
@RequestMapping("/api/v1/customers")
@SecurityRequirement(name = "apiKeyAuth")
public class CustomersController {

    @PostMapping
    public ResponseEntity<Void> create() {return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();}

    @GetMapping("/{id}")
    public ResponseEntity<Void> get(@PathVariable Long id) { return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id) { return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }

    @GetMapping
    public ResponseEntity<Void> list() { return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); }

}
