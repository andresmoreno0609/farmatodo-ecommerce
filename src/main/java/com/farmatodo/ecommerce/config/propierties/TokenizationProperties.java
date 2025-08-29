package com.farmatodo.ecommerce.config.propierties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "tokenization")
public class TokenizationProperties {
    /** Probabilidad de rechazo en tokenización (0..1) */
    private double rejectionProbability = 0.2;
}
