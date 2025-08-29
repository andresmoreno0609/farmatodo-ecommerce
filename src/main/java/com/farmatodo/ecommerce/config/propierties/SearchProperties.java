package com.farmatodo.ecommerce.config.propierties;


import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "search")
public class SearchProperties {
    private int minStock = 1;
}
