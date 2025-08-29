package com.farmatodo.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name="order_items")
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Column(nullable = false)
    private Long productId;
    @Column(nullable = false)
    private String productSku;
    @Column(nullable = false)
    private String productName;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;
    @Column(nullable = false)
    private Integer quantity;
}
