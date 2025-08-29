package com.farmatodo.ecommerce.entity;

import com.farmatodo.ecommerce.enums.EPaymentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name="payments")
public class PaymentEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @OneToOne(fetch=FetchType.LAZY) @JoinColumn(name="order_id", nullable=false, unique=true)
    private OrderEntity order;

    @Column(nullable=false) private String tokenizedCard;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private EPaymentStatus status;

    @Column(nullable=false) private Integer attempts;
    @Column private String lastError;
    @Column private OffsetDateTime lastTriedAt;
}
