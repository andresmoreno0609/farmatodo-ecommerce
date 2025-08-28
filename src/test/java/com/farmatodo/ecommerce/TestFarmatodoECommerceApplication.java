package com.farmatodo.ecommerce;

import org.springframework.boot.SpringApplication;

public class TestFarmatodoECommerceApplication {

	public static void main(String[] args) {
		SpringApplication.from(FarmatodoECommerceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
