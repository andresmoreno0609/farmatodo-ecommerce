package com.farmatodo.ecommerce.config.propierties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "payments")
public class PaymentsProperties {

    /** Probabilidad de aprobación del gateway (0..1) */
    private double approvalProbability = 0.8;

    private final Retry retry = new Retry();

    @Getter
    public static class Retry {
        /** Máximo de intentos */
        private int maxAttempts = 3;
        /** Backoff inicial en ms (si lo usas) */
        private long initialMs = 500;
        /** Multiplicador del backoff (si lo usas) */
        private double multiplier = 2.0;
        /** Cadencia del scheduler en segundos */
        private int cadenceSeconds = 30;

    }
}
