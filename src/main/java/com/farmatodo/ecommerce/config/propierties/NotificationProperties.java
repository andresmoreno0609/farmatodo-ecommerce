package com.farmatodo.ecommerce.config.propierties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "notifications")
public class NotificationProperties {
    private String operatorEmail;
}
