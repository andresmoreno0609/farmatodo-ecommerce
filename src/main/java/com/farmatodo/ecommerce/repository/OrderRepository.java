package com.farmatodo.ecommerce.repository;

import com.farmatodo.ecommerce.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {}
