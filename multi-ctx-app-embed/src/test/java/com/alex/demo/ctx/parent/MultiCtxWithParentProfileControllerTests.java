package com.alex.demo.ctx.parent;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("parent")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class MultiCtxWithParentProfileControllerTests {
    
	@Autowired
	TestRestTemplate restTemplate;

	@Autowired
	ApplicationContext applicationContext;

	@SuppressWarnings("unchecked")
	@Test
	void testChildWithParent() throws Exception {

		Map<String, String> response = restTemplate.getForObject("http://localhost:{port}/child", Map.class,
				getChildPort());
		
		Assertions.assertAll("Response from child context is wrong!",
		        () -> Assertions.assertEquals("parent_bean", response.get("parentBean")),
		        () -> Assertions.assertEquals("child_bean", response.get("childBean")),
		        () -> Assertions.assertEquals("common_prop", response.get("parentProperty")),
		        () -> Assertions.assertEquals("prop_child", response.get("childProperty"))
		);
	}

	@SuppressWarnings("unchecked")
	@Test
	void testChildNotExists() throws Exception {

		Map<String, ?> response = restTemplate.getForObject("http://localhost:{port}/child/dummy", Map.class,
				getChildPort());

		Assertions.assertAll("Error response for non-existing URL on child context is wrong!",
		        () -> Assertions.assertEquals("Not Found", response.get("error")),
		        () -> Assertions.assertEquals(new Integer(404), response.get("status")),
		        () -> Assertions.assertEquals("/child/dummy", response.get("path"))
		);
	}

	@SuppressWarnings("unchecked")
	@Test
	void testParent() throws Exception {

		Map<String, String> response = restTemplate.getForObject("/parent", Map.class);
		
		Assertions.assertAll("Response from parent context is wrong!",
		        () -> Assertions.assertEquals("parent_bean", response.get("parentBean")),
		        () -> Assertions.assertNull(response.get("childBean")),
		        () -> Assertions.assertEquals("common_prop", response.get("parentProperty")),
		        () -> Assertions.assertEquals("null", response.get("childProperty"))
		);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void testParentNotExists() throws Exception {

		Map<String, ?> response = restTemplate.getForObject("/parent/dummy", Map.class);

		Assertions.assertAll("Error response for non-existing URL on parent context is wrong!",
		        () -> Assertions.assertEquals("Not Found", response.get("error")),
		        () -> Assertions.assertEquals(new Integer(404), response.get("status")),
		        () -> Assertions.assertEquals("/parent/dummy", response.get("path"))
		);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void testActuator() throws Exception {

		Map<String, String> response = restTemplate.getForObject("http://localhost:8081/actuator/beans", Map.class);

		Assertions.assertNotNull(response.get("contexts"), "Actuator response is wrong!");
	}

	int getChildPort() {

		AnnotationConfigServletWebServerApplicationContext childContext = applicationContext
				.getBean(AnnotationConfigServletWebServerApplicationContext.class);
		return childContext.getWebServer().getPort();
	}
}
