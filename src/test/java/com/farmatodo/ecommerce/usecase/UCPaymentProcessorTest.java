package com.farmatodo.ecommerce.usecase;

import com.farmatodo.ecommerce.config.propierties.PaymentsProperties;
import com.farmatodo.ecommerce.entity.OrderEntity;
import com.farmatodo.ecommerce.entity.PaymentEntity;
import com.farmatodo.ecommerce.enums.EPaymentStatus;
import com.farmatodo.ecommerce.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UCPaymentProcessorTest {

    private PaymentRepository paymentRepo;
    private UCOrder ucOrder;
    private PaymentsProperties props;
    private PaymentsProperties.Retry retryProps;
    private UCEmail email;
    private UCPaymentProcessor processor;

    @BeforeEach
    void setUp() throws Exception {
        paymentRepo = mock(PaymentRepository.class);
        ucOrder = mock(UCOrder.class);
        props = mock(PaymentsProperties.class);
        retryProps = mock(PaymentsProperties.Retry.class);
        email = mock(UCEmail.class);

        when(props.getRetry()).thenReturn(retryProps);
        when(retryProps.getCadenceSeconds()).thenReturn(30); // usado en tick()

        processor = new UCPaymentProcessor(paymentRepo, ucOrder, props, email);

        setDeterministicRandom(processor, 0.0); // por defecto, fuerza "rechazo"
    }

    private void setDeterministicRandom(UCPaymentProcessor target, double value) throws Exception {
        class FixedRandom extends Random {
            private final double v;
            FixedRandom(double v) { this.v = v; }
            @Override public double nextDouble() { return v; }
        }
        Field f = UCPaymentProcessor.class.getDeclaredField("rng");
        f.setAccessible(true);
        f.set(target, new FixedRandom(value));
    }

    private PaymentEntity payment(long orderId, String total, int attempts, EPaymentStatus status) {
        var o = new OrderEntity();
        o.setId(orderId);
        o.setTotal(new BigDecimal(total));

        var p = new PaymentEntity();
        p.setOrder(o);
        p.setAttempts(attempts);
        p.setStatus(status);
        return p;
    }

    @Test
    void attempt_shouldApprove_andNotify_whenRandomBelowApprovalProbability() throws Exception {
        when(props.getApprovalProbability()).thenReturn(1.0); // 100% aprueba
        when(retryProps.getMaxAttempts()).thenReturn(3);

        setDeterministicRandom(processor, 0.0);

        var p = payment(101L, "50.00", 0, EPaymentStatus.PENDING);

        processor.attempt(p);

        ArgumentCaptor<PaymentEntity> cap = ArgumentCaptor.forClass(PaymentEntity.class);
        verify(paymentRepo, atLeastOnce()).save(cap.capture());
        assertThat(cap.getAllValues().get(cap.getAllValues().size()-1).getStatus())
                .isEqualTo(EPaymentStatus.APPROVED);

        verify(ucOrder).markPaid(p.getOrder());
        verify(email).sendPaymentSuccess(p.getOrder());

        assertThat(p.getAttempts()).isEqualTo(1);
        assertThat(p.getLastError()).isNull();
    }

    @Test
    void attempt_shouldRemainPending_andIncrementAttempts_whenDeclined_belowMax() {
        when(props.getApprovalProbability()).thenReturn(0.0); // 0% aprueba => siempre declina
        when(retryProps.getMaxAttempts()).thenReturn(3);

        var p = payment(7L, "10.00", 0, EPaymentStatus.PENDING);

        processor.attempt(p);

        ArgumentCaptor<PaymentEntity> cap = ArgumentCaptor.forClass(PaymentEntity.class);
        verify(paymentRepo, atLeastOnce()).save(cap.capture());
        PaymentEntity lastSave = cap.getAllValues().get(cap.getAllValues().size()-1);
        assertThat(lastSave.getStatus()).isEqualTo(EPaymentStatus.PENDING);
        assertThat(lastSave.getLastError()).isEqualTo("gateway_declined");

        verify(ucOrder, never()).markFailed(any());
        verify(email, never()).sendPaymentFailed(any(), anyInt(), anyString());

        assertThat(p.getAttempts()).isEqualTo(1);
    }

    @Test
    void attempt_shouldFail_andNotify_whenDeclined_reachesMaxAttempts() {
        when(props.getApprovalProbability()).thenReturn(0.0); // siempre rechaza
        when(retryProps.getMaxAttempts()).thenReturn(2);

        var p = payment(9L, "99.00", /*attempts=*/0, EPaymentStatus.PENDING);

        processor.attempt(p);

// último save debe ser PENDING
        ArgumentCaptor<PaymentEntity> cap = ArgumentCaptor.forClass(PaymentEntity.class);
        verify(paymentRepo, atLeastOnce()).save(cap.capture());
        PaymentEntity last = cap.getAllValues().get(cap.getAllValues().size()-1);
        assertThat(last.getStatus()).isEqualTo(EPaymentStatus.PENDING);
        assertThat(p.getAttempts()).isEqualTo(1);
        verify(ucOrder, never()).markFailed(any());
        verify(email, never()).sendPaymentFailed(any(), anyInt(), anyString());
    }

    @Test
    void tick_shouldFetchPendingsAndAttemptEach() throws Exception {
        when(props.getApprovalProbability()).thenReturn(0.0); // declina
        when(retryProps.getMaxAttempts()).thenReturn(10);     // no alcanza máximo
        setDeterministicRandom(processor, 0.0);

        var p1 = payment(1L, "1.00", 0, EPaymentStatus.PENDING);
        var p2 = payment(2L, "2.00", 2, EPaymentStatus.PENDING);
        when(paymentRepo.findByStatusAndLastTriedAtBefore(eq(EPaymentStatus.PENDING), any(OffsetDateTime.class)))
                .thenReturn(List.of(p1, p2));

        processor.tick();

        verify(paymentRepo, atLeast(2)).save(any(PaymentEntity.class));

        verify(paymentRepo).findByStatusAndLastTriedAtBefore(eq(EPaymentStatus.PENDING), any(OffsetDateTime.class));
    }
}
