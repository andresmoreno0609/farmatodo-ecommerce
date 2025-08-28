package com.farmatodo.ecommerce.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Notifications")
@RestController
@RequestMapping("/api/v1/notifications")
@SecurityRequirement(name = "apiKeyAuth")
public class NotificationsController {
    @PostMapping("/test-email")
    public ResponseEntity<Void> testEmail() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
