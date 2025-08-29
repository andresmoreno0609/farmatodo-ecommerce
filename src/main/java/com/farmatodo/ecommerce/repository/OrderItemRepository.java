package com.farmatodo.ecommerce.repository;

import com.farmatodo.ecommerce.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {}
