package com.farmatodo.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name="product_search_log")
public class ProductSearchLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String queryText;

    private Integer minStock;

    private Long results;

    private OffsetDateTime createdAt = OffsetDateTime.now();

    public ProductSearchLogEntity(String q, Integer m, Long r){
        this.queryText=q; this.minStock=m; this.results=r;
    }
}
