package com.farmatodo.ecommerce;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "spring.testcontainers", name = "enabled", havingValue = "true")
class TestcontainersConfigurationIT {

	@Bean(destroyMethod = "stop")
	public PostgreSQLContainer<?> postgresContainer() {
		PostgreSQLContainer<?> pg = new PostgreSQLContainer<>(
				DockerImageName.parse("postgres:16-alpine"))
				.withDatabaseName("test_db")
				.withUsername("test")
				.withPassword("test");
		pg.start();
		return pg;
	}

}
