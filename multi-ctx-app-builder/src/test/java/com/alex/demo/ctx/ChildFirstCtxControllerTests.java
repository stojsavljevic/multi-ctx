package com.alex.demo.ctx;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;

import com.alex.demo.ctx.child.first.ChildFirstCtxConfig;

/**
 * In this scenario we can test contexts separately because we have two
 * independent {@link SpringBootApplication}. We have to provide configuration
 * class for parent context only because we need one bean from there.
 *
 */
@ContextHierarchy(@ContextConfiguration(name = "child", classes = ChildFirstCtxConfig.class))
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class ChildFirstCtxControllerTests extends ParentCtxDefinition {

	@Autowired
	TestRestTemplate restTemplate;

	@SuppressWarnings("unchecked")
	@Test
	void testChildFirst() throws Exception {

		Map<String, String> response = this.restTemplate.getForObject("/", Map.class);
		
		Assertions.assertAll("Response from the first child context is wrong!",
		        () -> Assertions.assertEquals("parent_bean", response.get("parentBean")),
		        () -> Assertions.assertEquals("child_first_bean", response.get("childFirstBean")),
		        () -> Assertions.assertNull(response.get("childSecondBean")),
		        () -> Assertions.assertEquals("common_prop", response.get("parentProperty")),
		        () -> Assertions.assertEquals("prop_first", response.get("childFirstProperty")),
		        () -> Assertions.assertEquals("null", response.get("childSecondProperty"))
		);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void testChildFirstNotExists() throws Exception {

		Map<String, ?> response = this.restTemplate.getForObject("/dummy", Map.class);
		
		Assertions.assertAll("Error response for non-existing URL on the first child context is wrong!",
		        () -> Assertions.assertEquals("Not Found", response.get("error")),
		        () -> Assertions.assertEquals(404, response.get("status")),
		        () -> Assertions.assertEquals("/first/dummy", response.get("path"))
		);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void testChildFirstActuator() throws Exception {

		Map<String, String> response = this.restTemplate.getForObject("/actuator/beans", Map.class);
		
		Assertions.assertNotNull(response.get("contexts"), "Actuator response is wrong!");
	}
}
