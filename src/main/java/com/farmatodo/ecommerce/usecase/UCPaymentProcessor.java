package com.farmatodo.ecommerce.usecase;


import com.farmatodo.ecommerce.config.propierties.PaymentsProperties;
import com.farmatodo.ecommerce.entity.PaymentEntity;
import com.farmatodo.ecommerce.enums.EPaymentStatus;
import com.farmatodo.ecommerce.repository.PaymentRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Random;

@Component
public class UCPaymentProcessor {

    private final PaymentRepository paymentRepo;
    private final UCOrder ucOrder;
    private final PaymentsProperties properties;
    private final UCEmail emailService;
    private final Random rng = new Random();

    public UCPaymentProcessor(PaymentRepository paymentRepo, UCOrder ucOrder, PaymentsProperties properties, UCEmail emailService){
        this.paymentRepo = paymentRepo; this.ucOrder = ucOrder; this.properties = properties;
        this.emailService = emailService;
    }

    // corre cada X segundos, toma PENDING listos para reintento
    @Scheduled(fixedDelayString = "#{${settings.payment-retry-delay-seconds:30}*1000}")
    @Transactional
    public void tick(){
        var before = OffsetDateTime.now().minusSeconds(properties.getRetry().getCadenceSeconds());
        var pendings = paymentRepo.findByStatusAndLastTriedAtBefore(EPaymentStatus.PENDING, before);
        for (var p : pendings){
            attempt(p);
        }
    }

    @Transactional
    public void attempt(PaymentEntity p){
        p.setLastTriedAt(OffsetDateTime.now());
        p.setAttempts(p.getAttempts()+1);

        boolean approved = rng.nextDouble() < properties.getApprovalProbability();
        if (approved){
            p.setStatus(EPaymentStatus.APPROVED);
            p.setLastError(null);
            paymentRepo.save(p);
            ucOrder.markPaid(p.getOrder());

            emailService.sendPaymentSuccess(p.getOrder());
            return;
        }

        p.setStatus(EPaymentStatus.PENDING);
        p.setLastError("gateway_declined");
        paymentRepo.save(p);

        if (p.getAttempts() >= properties.getApprovalProbability()){

            p.setStatus(EPaymentStatus.FAILED);
            paymentRepo.save(p);
            ucOrder.markFailed(p.getOrder());
            emailService.sendPaymentFailed(p.getOrder(), p.getAttempts(), p.getLastError());
        }
    }
}
