package com.farmatodo.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "tokenized_cards")
public class TokenizedCardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=64)
    private String token;

    @Column(nullable=false, length=8)
    private String last4;

    @Column(nullable=false, length=20)
    private String brand;

    @Column(nullable=false, columnDefinition="text")
    private String encryptedPayload;

    @Column(nullable=false)
    private Instant createdAt = Instant.now();
}
