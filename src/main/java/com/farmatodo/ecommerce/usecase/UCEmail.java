package com.farmatodo.ecommerce.usecase;

import com.farmatodo.ecommerce.config.propierties.NotificationProperties;
import com.farmatodo.ecommerce.entity.OrderEntity;
import com.farmatodo.ecommerce.repository.CustomerRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class UCEmail {
    private final JavaMailSender mailSender;
    private final CustomerRepository customerRepo;
    private final NotificationProperties props;

    public UCEmail(JavaMailSender mailSender,
                   CustomerRepository customerRepo,
                   NotificationProperties props) {
        this.mailSender = mailSender;
        this.customerRepo = customerRepo;
        this.props = props;
    }

    private void sendCopyToOperator(String subject, String text) {
        if (props.getOperatorEmail() != null && !props.getOperatorEmail().isBlank()) {
            var copy = new SimpleMailMessage();
            copy.setTo(props.getOperatorEmail());
            copy.setSubject("[COPIA] " + subject);
            copy.setText(text);
            mailSender.send(copy);
        }
    }

    @Async
    public void sendPaymentSuccess(OrderEntity order) {
        var to = customerRepo.findById(order.getCustomerId())
                .map(c -> c.getEmail()).orElse("customer@mail.test");
        var subject = " Pago aprobado - Pedido #" + order.getId();
        var text = "Pedido #" + order.getId() + " aprobado. Total: " + order.getTotal();
        var msg = new SimpleMailMessage();
        msg.setTo(to); msg.setSubject(subject); msg.setText(text);
        mailSender.send(msg);
        sendCopyToOperator(subject, "Se notificó a " + to + ". " + text);
    }

    @Async
    public void sendPaymentFailed(OrderEntity order, int attempts, String reason) {
        var to = customerRepo.findById(order.getCustomerId())
                .map(c -> c.getEmail()).orElse("customer@mail.test");
        var subject = "Pago fallido - Pedido #" + order.getId();
        var text = "Intentos: " + attempts + ". Motivo: " + (reason == null ? "gateway_declined" : reason);
        var msg = new SimpleMailMessage();
        msg.setTo(to); msg.setSubject(subject); msg.setText(text);
        mailSender.send(msg);
        sendCopyToOperator(subject, "Se notificó a " + to + ". " + text);
    }
}
