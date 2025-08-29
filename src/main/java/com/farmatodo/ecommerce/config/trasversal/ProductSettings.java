package com.farmatodo.ecommerce.config.trasversal;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// config/SettingsConfig.java
@Configuration
@ConfigurationProperties(prefix="settings")
public class ProductSettings {
    private int minVisibleStock = 1;
    public int getMinVisibleStock(){ return minVisibleStock; }
    public void setMinVisibleStock(int v){ this.minVisibleStock = v; }
}
