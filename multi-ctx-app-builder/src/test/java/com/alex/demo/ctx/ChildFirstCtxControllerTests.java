package com.alex.demo.ctx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringRunner;

import com.alex.demo.ctx.child.first.ChildFirstCtxConfig;

/**
 * In this scenario we can test contexts separately because we have two
 * independent {@link SpringBootApplication}. We have to provide configuration
 * class for parent context only because we need one bean from there.
 *
 */
@RunWith(SpringRunner.class)
@ContextHierarchy(@ContextConfiguration(name = "child", classes = ChildFirstCtxConfig.class))
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class ChildFirstCtxControllerTests extends ParentCtxDefinition {

    // prior to Spring Boot 2.3 it was "No message available" but now it's empty
    private static final String ERROR_MESSAGE = "";
    
	@Autowired
	TestRestTemplate restTemplate;

	@SuppressWarnings("unchecked")
	@Test
	public void testChildFirst() throws Exception {

		Map<String, String> response = restTemplate.getForObject("/", Map.class);

		assertEquals("parent_bean", response.get("parentBean"));
		assertEquals("child_first_bean", response.get("childFirstBean"));
		assertNull(response.get("childSecondBean"));
		assertEquals("common_prop", response.get("parentProperty"));
		assertEquals("prop_first", response.get("childFirstProperty"));
		assertEquals("null", response.get("childSecondProperty"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testChildFirstNotExists() throws Exception {

		Map<String, String> response = restTemplate.getForObject("/dummy", Map.class);

		assertEquals("Not Found", response.get("error"));
		assertEquals(ERROR_MESSAGE, response.get("message"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testChildFirstActuator() throws Exception {

		Map<String, String> response = restTemplate.getForObject("/actuator/beans", Map.class);

		assertNotNull(response.get("contexts"));
	}
}
