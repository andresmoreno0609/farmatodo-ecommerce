package com.farmatodo.ecommerce.config.trasversal;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// config/SettingsConfig.java
@Configuration
@ConfigurationProperties(prefix="settings")
public class SettingsConfig {
    private int minVisibleStock = 1;
    public int getMinVisibleStock(){ return minVisibleStock; }
    public void setMinVisibleStock(int v){ this.minVisibleStock = v; }

    private double paymentApprovalProbability = 0.7;
    private int paymentMaxRetries = 3;
    private int paymentRetryDelaySeconds = 30;

    public double getPaymentApprovalProbability(){ return paymentApprovalProbability; }
    public void setPaymentApprovalProbability(double v){ this.paymentApprovalProbability = v; }
    public int getPaymentMaxRetries(){ return paymentMaxRetries; }
    public void setPaymentMaxRetries(int v){ this.paymentMaxRetries = v; }
    public int getPaymentRetryDelaySeconds(){ return paymentRetryDelaySeconds; }
    public void setPaymentRetryDelaySeconds(int v){ this.paymentRetryDelaySeconds = v; }
}
