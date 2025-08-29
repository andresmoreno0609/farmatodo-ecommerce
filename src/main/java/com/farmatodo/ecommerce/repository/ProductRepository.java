package com.farmatodo.ecommerce.repository;

import com.farmatodo.ecommerce.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    boolean existsBySku(String sku);
    boolean existsBySkuAndIdNot(String sku, Long id);

    @Query("""
           SELECT p FROM ProductEntity p
           WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))
                  OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :q, '%')))
             AND p.stock >= :minStock
             AND p.status <> com.farmatodo.ecommerce.enums.ERecordStatus.DELETED
           """)
    Page<ProductEntity> searchByNameOrSku(@Param("q") String q,
                                          @Param("minStock") int minStock,
                                          Pageable pageable);
}
