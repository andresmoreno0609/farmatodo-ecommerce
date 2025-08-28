package com.farmatodo.ecommerce.repository;

import com.farmatodo.ecommerce.entity.TokenizedCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenizedCardRepository extends JpaRepository<TokenizedCardEntity, Long> {
    Optional<TokenizedCardEntity> findByToken(String token);
}
