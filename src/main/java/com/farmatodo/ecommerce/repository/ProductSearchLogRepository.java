package com.farmatodo.ecommerce.repository;

import com.farmatodo.ecommerce.entity.ProductSearchLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSearchLogRepository extends JpaRepository<ProductSearchLogEntity, Long> {
}
