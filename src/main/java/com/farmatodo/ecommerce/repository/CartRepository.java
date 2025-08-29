package com.farmatodo.ecommerce.repository;

import com.farmatodo.ecommerce.entity.CartEntity;
import org.springframework.data.jpa.repository.*;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartEntity, Long> {

    Optional<CartEntity> findByCustomerId(Long customerId);

    @EntityGraph(attributePaths = "items")
    Optional<CartEntity> findWithItemsByCustomerId(Long customerId);
}
