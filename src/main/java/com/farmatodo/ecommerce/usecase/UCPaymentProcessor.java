package com.farmatodo.ecommerce.usecase;


import com.farmatodo.ecommerce.config.trasversal.SettingsConfig;
import com.farmatodo.ecommerce.entity.PaymentEntity;
import com.farmatodo.ecommerce.enums.EPaymentStatus;
import com.farmatodo.ecommerce.repository.PaymentRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Random;

@Component
public class UCPaymentProcessor {

    private final PaymentRepository paymentRepo;
    private final UCOrder ucOrder;
    private final SettingsConfig settings;
    private final JavaMailSender mailSender;
    private final Random rng = new Random();

    public UCPaymentProcessor(PaymentRepository paymentRepo, UCOrder ucOrder, SettingsConfig settings, JavaMailSender mailSender){
        this.paymentRepo = paymentRepo; this.ucOrder = ucOrder; this.settings = settings; this.mailSender = mailSender;
    }

    // corre cada X segundos, toma PENDING listos para reintento
    @Scheduled(fixedDelayString = "#{${settings.payment-retry-delay-seconds:30}*1000}")
    @Transactional
    public void tick(){
        var before = OffsetDateTime.now().minusSeconds(settings.getPaymentRetryDelaySeconds());
        var pendings = paymentRepo.findByStatusAndLastTriedAtBefore(EPaymentStatus.PENDING, before);
        for (var p : pendings){
            attempt(p);
        }
    }

    @Transactional
    public void attempt(PaymentEntity p){
        p.setLastTriedAt(OffsetDateTime.now());
        p.setAttempts(p.getAttempts()+1);

        boolean approved = rng.nextDouble() < settings.getPaymentApprovalProbability();
        if (approved){
            p.setStatus(EPaymentStatus.APPROVED);
            p.setLastError(null);
            paymentRepo.save(p);
            ucOrder.markPaid(p.getOrder());
            return;
        }
        // Rechazado este intento
        p.setStatus(EPaymentStatus.PENDING);
        p.setLastError("gateway_declined");
        paymentRepo.save(p);

        if (p.getAttempts() >= settings.getPaymentMaxRetries()){
            // falló definitivamente
            p.setStatus(EPaymentStatus.FAILED);
            paymentRepo.save(p);
            ucOrder.markFailed(p.getOrder());
            notifyCustomer(p);
        }
    }

    private void notifyCustomer(PaymentEntity p){
        // En un real, buscaríamos el email del cliente en Customers.
        var to = "customer@mail.test";
        var msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Pago fallido para pedido #" + p.getOrder().getId());
        msg.setText("No fue posible procesar tu pago tras " + p.getAttempts() + " intentos.");
        mailSender.send(msg);
    }
}
