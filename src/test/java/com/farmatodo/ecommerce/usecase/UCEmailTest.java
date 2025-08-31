package com.farmatodo.ecommerce.usecase;

import com.farmatodo.ecommerce.config.propierties.NotificationProperties;
import com.farmatodo.ecommerce.entity.CustomerEntity;
import com.farmatodo.ecommerce.entity.OrderEntity;
import com.farmatodo.ecommerce.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UCEmailTest {

    private JavaMailSender mailSender;
    private CustomerRepository customerRepo;
    private NotificationProperties props;
    private UCEmail uc;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        customerRepo = mock(CustomerRepository.class);
        props = mock(NotificationProperties.class);
        uc = new UCEmail(mailSender, customerRepo, props);
    }

    private OrderEntity order(long id, long customerId, String total) {
        var o = new OrderEntity();
        o.setId(id);
        o.setCustomerId(customerId);
        o.setTotal(new BigDecimal(total));
        return o;
    }

    private CustomerEntity customer(String email) {
        var c = new CustomerEntity();
        c.setEmail(email);
        return c;
    }

    @Test
    void sendPaymentSuccess_shouldSendToCustomer_andCopyToOperator_whenOperatorConfigured() {
        var o = order(101L, 5L, "123.45");
        when(customerRepo.findById(5L)).thenReturn(Optional.of(customer("cliente@test.com")));
        when(props.getOperatorEmail()).thenReturn("ops@test.com");

        ArgumentCaptor<SimpleMailMessage> msgCap = ArgumentCaptor.forClass(SimpleMailMessage.class);

        uc.sendPaymentSuccess(o);

        verify(mailSender, times(2)).send(msgCap.capture());

        var msgs = msgCap.getAllValues();
        var m1 = msgs.get(0);
        assertThat(m1.getTo()).containsExactly("cliente@test.com");
        assertThat(m1.getSubject()).isEqualTo(" Pago aprobado - Pedido #101");
        assertThat(m1.getText()).isEqualTo("Pedido #101 aprobado. Total: 123.45");

        var m2 = msgs.get(1);
        assertThat(m2.getTo()).containsExactly("ops@test.com");
        assertThat(m2.getSubject()).isEqualTo("[COPIA]  Pago aprobado - Pedido #101");
        assertThat(m2.getText()).isEqualTo("Se notificó a cliente@test.com. Pedido #101 aprobado. Total: 123.45");
    }

    @Test
    void sendPaymentSuccess_shouldUseFallbackEmail_andNotSendCopy_whenOperatorBlank() {
        var o = order(7L, 9L, "10.00");
        when(customerRepo.findById(9L)).thenReturn(Optional.empty()); // no existe el cliente
        when(props.getOperatorEmail()).thenReturn("   "); // en blanco

        ArgumentCaptor<SimpleMailMessage> msgCap = ArgumentCaptor.forClass(SimpleMailMessage.class);

        uc.sendPaymentSuccess(o);

        verify(mailSender, times(1)).send(msgCap.capture());

        var msg = msgCap.getValue();
        assertThat(msg.getTo()).containsExactly("customer@mail.test");
        assertThat(msg.getSubject()).isEqualTo(" Pago aprobado - Pedido #7");
        assertThat(msg.getText()).isEqualTo("Pedido #7 aprobado. Total: 10.00");
    }

    @Test
    void sendPaymentFailed_shouldSendToCustomer_andCopyToOperator_withProvidedReason() {
        var o = order(55L, 2L, "89.00");
        when(customerRepo.findById(2L)).thenReturn(Optional.of(customer("c2@test.com")));
        when(props.getOperatorEmail()).thenReturn("ops@test.com");

        ArgumentCaptor<SimpleMailMessage> msgCap = ArgumentCaptor.forClass(SimpleMailMessage.class);

        uc.sendPaymentFailed(o, 3, "insufficient_funds");

        verify(mailSender, times(2)).send(msgCap.capture());
        var msgs = msgCap.getAllValues();

        var m1 = msgs.get(0);
        assertThat(m1.getTo()).containsExactly("c2@test.com");
        assertThat(m1.getSubject()).isEqualTo("Pago fallido - Pedido #55");
        assertThat(m1.getText()).isEqualTo("Intentos: 3. Motivo: insufficient_funds");

        var m2 = msgs.get(1);
        assertThat(m2.getTo()).containsExactly("ops@test.com");
        assertThat(m2.getSubject()).isEqualTo("[COPIA] Pago fallido - Pedido #55");
        assertThat(m2.getText()).isEqualTo("Se notificó a c2@test.com. Intentos: 3. Motivo: insufficient_funds");
    }

    @Test
    void sendPaymentFailed_shouldDefaultReason_whenNull() {
        var o = order(9L, 3L, "1.00");
        when(customerRepo.findById(3L)).thenReturn(Optional.of(customer("x@test.com")));
        when(props.getOperatorEmail()).thenReturn("ops@test.com");

        ArgumentCaptor<SimpleMailMessage> msgCap = ArgumentCaptor.forClass(SimpleMailMessage.class);

        uc.sendPaymentFailed(o, 2, null);

        verify(mailSender, times(2)).send(msgCap.capture());
        var msgs = msgCap.getAllValues();

        assertThat(msgs.get(0).getText()).isEqualTo("Intentos: 2. Motivo: gateway_declined");
        assertThat(msgs.get(1).getText()).isEqualTo("Se notificó a x@test.com. Intentos: 2. Motivo: gateway_declined");
    }
}
