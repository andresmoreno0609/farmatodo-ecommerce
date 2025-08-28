package com.farmatodo.ecommerce.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Tokenization")
@RestController
@RequestMapping("/api/v1/tokenize")
@SecurityRequirement(name = "apiKeyAuth")
public class TokenizationController {

    @PostMapping
    public ResponseEntity<Void> tokenize() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
