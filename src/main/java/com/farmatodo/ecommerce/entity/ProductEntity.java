package com.farmatodo.ecommerce.entity;

import com.farmatodo.ecommerce.enums.ERecordStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "products", uniqueConstraints = {
        @UniqueConstraint(name = "uk_products_sku", columnNames = "sku")
})
public class ProductEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=80)
    private String sku;

    @Column(nullable=false, length=160)
    private String name;

    @Column(length=500)
    private String description;

    @Column(nullable=false, precision=12, scale=2)
    private BigDecimal price;

    @Column(nullable=false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private ERecordStatus status;

    @CreationTimestamp @Column(name="created_at", updatable=false, nullable=false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp  @Column(name="updated_at", nullable=false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void onCreate() {
        var now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) status = ERecordStatus.ACTIVE;
    }

    @PreUpdate
    void onUpdate() { updatedAt = OffsetDateTime.now(); }

}
