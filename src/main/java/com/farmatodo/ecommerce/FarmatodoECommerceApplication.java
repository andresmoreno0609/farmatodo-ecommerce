package com.farmatodo.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FarmatodoECommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FarmatodoECommerceApplication.class, args);
	}

}
