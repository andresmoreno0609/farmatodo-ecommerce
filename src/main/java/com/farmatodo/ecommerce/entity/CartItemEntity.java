package com.farmatodo.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "cart_items", uniqueConstraints = {
        @UniqueConstraint(name="uk_cart_item_unique", columnNames = {"cart_id","product_id"})
})
public class CartItemEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="cart_id", nullable=false)
    private CartEntity cart;

    @Column(name="product_id", nullable=false) private Long productId;
    @Column(nullable=false) private Integer quantity;

    // datos desnormalizados para mostrar sin join pesado
    @Column(name="product_name", nullable=false, length=160) private String productName;
    @Column(name="product_sku",  nullable=false, length=80)  private String productSku;
    @Column(name="unit_price",   nullable=false, precision=12, scale=2) private BigDecimal unitPrice;

}
