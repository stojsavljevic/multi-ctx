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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class MultiCtxControllerTests {

	@Autowired
	TestRestTemplate restTemplate;

	@SuppressWarnings("unchecked")
	@Test
	public void testChild() throws Exception {

		Map<String, String> response = restTemplate.getForObject("http://localhost:8082/child", Map.class);

		assertNull(response.get("parentBean"));
		assertEquals("child_bean", response.get("childBean"));
		assertEquals("null", response.get("parentProperty"));
		assertEquals("prop_child", response.get("childProperty"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testChildNotExists() throws Exception {

		Map<String, String> response = restTemplate.getForObject("http://localhost:8082/child/dummy", Map.class);

		assertEquals("Not Found", response.get("error"));
		assertEquals("No message available", response.get("message"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testParent() throws Exception {

		Map<String, String> response = restTemplate.getForObject("http://localhost:8080/parent", Map.class);

		assertEquals("parent_bean", response.get("parentBean"));
		assertNull(response.get("childBean"));
		assertEquals("common_prop", response.get("parentProperty"));
		assertEquals("null", response.get("childProperty"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testParentNotExists() throws Exception {

		Map<String, String> response = restTemplate.getForObject("http://localhost:8080/parent/dummy", Map.class);

		assertEquals("Not Found", response.get("error"));
		assertEquals("No message available", response.get("message"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testActuator() throws Exception {

		Map<String, String> response = restTemplate.getForObject("http://localhost:8081/actuator/beans", Map.class);

		assertNotNull(response.get("contexts"));
	}
}
