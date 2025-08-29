package com.farmatodo.ecommerce.entity;

import com.farmatodo.ecommerce.enums.ERecordStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "customers",
        uniqueConstraints = {
                @UniqueConstraint(name="uk_customers_email", columnNames="email"),
                @UniqueConstraint(name="uk_customers_phone", columnNames="phone")
        })
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=120)
    private String name;

    @Column(nullable=false, length=120)
    private String email;

    @Column(nullable=false, length=30)
    private String phone;

    @Column(length=255)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private ERecordStatus status;

    @Column(nullable=false)
    private OffsetDateTime createdAt;

    @Column(nullable=false)
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
