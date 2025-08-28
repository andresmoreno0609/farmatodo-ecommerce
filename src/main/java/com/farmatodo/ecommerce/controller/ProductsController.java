package com.farmatodo.ecommerce.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Products")
@RestController
@RequestMapping("/api/v1/products")
@SecurityRequirement(name = "apiKeyAuth")
public class ProductsController {

    @GetMapping
    public ResponseEntity<Void> search(@RequestParam(required = false) String q,
                                       @RequestParam(required = false) Integer minStock) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

}
