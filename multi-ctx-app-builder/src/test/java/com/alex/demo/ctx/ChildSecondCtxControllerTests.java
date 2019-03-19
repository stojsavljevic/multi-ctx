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

import com.alex.demo.ctx.child.second.ChildSecondCtxConfig;

/**
 * In this scenario we can test contexts separately because we have two
 * independent {@link SpringBootApplication}. We have to provide configuration
 * class for parent context only because we need one bean from there.
 *
 */
@RunWith(SpringRunner.class)
@ContextHierarchy(@ContextConfiguration(name = "child", classes = ChildSecondCtxConfig.class))
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class ChildSecondCtxControllerTests extends ParentCtxDefinition {

	@Autowired
	TestRestTemplate restTemplate;

	@SuppressWarnings("unchecked")
	@Test
	public void testChildSecond() throws Exception {

		Map<String, String> response = restTemplate.getForObject("/", Map.class);

		assertEquals("parent_bean", response.get("parentBean"));
		assertNull(response.get("childFirstBean"));
		assertEquals("child_second_bean", response.get("childSecondBean"));
		assertEquals("common_prop", response.get("parentProperty"));
		assertEquals("null", response.get("childFirstProperty"));
		assertEquals("prop_second", response.get("childSecondProperty"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testChildSecondNotExists() throws Exception {

		Map<String, String> response = restTemplate.getForObject("/dummy", Map.class);

		assertEquals("Not Found", response.get("error"));
		assertEquals("No message available", response.get("message"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testChildSecondActuator() throws Exception {

		Map<String, String> response = restTemplate.getForObject("/actuator/beans", Map.class);

		assertNotNull(response.get("contexts"));
	}
}
