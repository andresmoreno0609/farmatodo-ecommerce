package com.farmatodo.ecommerce;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfigurationIT.class)
@SpringBootTest
class FarmatodoECommerceApplicationTests {

	@Test
	void contextLoads() {
	}

}
