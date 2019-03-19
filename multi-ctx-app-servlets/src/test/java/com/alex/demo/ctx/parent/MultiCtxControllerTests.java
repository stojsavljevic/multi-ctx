package com.alex.demo.ctx.parent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MultiCtxControllerTests {

	@Autowired
	TestRestTemplate restTemplate;

	@SuppressWarnings("unchecked")
	@Test
	public void testParent() throws Exception {

		Map<String, String> response = restTemplate.getForObject("/", Map.class);

		assertEquals("parent_bean", response.get("parentBean"));
		assertNull(response.get("childFirstBean"));
		assertNull(response.get("childSecondBean"));
		assertEquals("common_prop", response.get("parentProperty"));
		assertEquals("null", response.get("childFirstProperty"));
		assertEquals("null", response.get("childSecondProperty"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testParentNotExists() throws Exception {

		Map<String, String> response = restTemplate.getForObject("/dummy", Map.class);

		assertEquals("Not Found", response.get("error"));
		assertEquals("No message available", response.get("message"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testParentActuator() throws Exception {

		Map<String, String> response = restTemplate.getForObject("/actuator/beans", Map.class);

		assertNotNull(response.get("contexts"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testChildFirst() throws Exception {

		Map<String, String> response = restTemplate.getForObject("/first/", Map.class);

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

		Map<String, String> response = restTemplate.getForObject("/first/dummy", Map.class);

		assertEquals("Not Found", response.get("error"));
		assertEquals("No message available", response.get("message"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testChildFirstActuator() throws Exception {

		Map<String, String> response = restTemplate.getForObject("/first/actuator/beans", Map.class);

		assertNotNull(response.get("contexts"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testChildSecond() throws Exception {

		Map<String, String> response = restTemplate.getForObject("/second/", Map.class);

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

		Map<String, String> response = restTemplate.getForObject("/second/dummy", Map.class);

		assertEquals("Not Found", response.get("error"));
		assertEquals("No message available", response.get("message"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testChildSecondActuator() throws Exception {

		Map<String, String> response = restTemplate.getForObject("/second/actuator/beans", Map.class);

		assertNotNull(response.get("contexts"));
	}
}
