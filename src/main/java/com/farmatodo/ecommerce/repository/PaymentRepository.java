package com.farmatodo.ecommerce.repository;

import com.farmatodo.ecommerce.entity.PaymentEntity;
import com.farmatodo.ecommerce.enums.EPaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.OffsetDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    List<PaymentEntity> findByStatusAndLastTriedAtBefore(EPaymentStatus status, OffsetDateTime before);
}
