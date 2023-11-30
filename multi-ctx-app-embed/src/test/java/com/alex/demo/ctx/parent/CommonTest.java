package com.alex.demo.ctx.parent;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = "child.server.port=0")
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
abstract class CommonTest {

	@Autowired
	TestRestTemplate restTemplate;
	
	@Autowired
	AnnotationConfigServletWebServerApplicationContext applicationContext;
	
	@Value("${local.management.port}")
	String actuatorPort;
	
	@SuppressWarnings("unchecked")
	@Test
	void testActuator() throws Exception {

		Map<String, String> response = restTemplate.getForObject("http://localhost:{actuatorPort}/actuator/beans", Map.class, this.actuatorPort);

		Assertions.assertNotNull(response.get("contexts"), "Actuator response is wrong!");
	}
	
	int getChildPort() {

		AnnotationConfigServletWebServerApplicationContext childContext = applicationContext
				.getBean(AnnotationConfigServletWebServerApplicationContext.class);
		return childContext.getWebServer().getPort();
	}
}
