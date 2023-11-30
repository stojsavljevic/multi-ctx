package com.alex.demo.ctx.parent;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MultiCtxControllerTests {
    
	@Autowired
	TestRestTemplate restTemplate;

	@SuppressWarnings("unchecked")
	@Test
	void testParent() throws Exception {

		Map<String, String> response = restTemplate.getForObject("/", Map.class);

		Assertions.assertAll("Response from parent context is wrong!",
				() -> Assertions.assertEquals("parent_bean", response.get("parentBean")),
		        () -> Assertions.assertNull(response.get("childFirstBean")),
		        () -> Assertions.assertNull(response.get("childSecondBean")),
		        () -> Assertions.assertEquals("common_prop", response.get("parentProperty")),
		        () -> Assertions.assertEquals("null", response.get("childFirstProperty")),
		        () -> Assertions.assertEquals("null", response.get("childSecondProperty"))
		);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void testParentNotExists() throws Exception {

		Map<String, ?> response = restTemplate.getForObject("/dummy", Map.class);

		Assertions.assertAll("Error response for non-existing URL on parent context is wrong!",
		        () -> Assertions.assertEquals("Not Found", response.get("error")),
		        () -> Assertions.assertEquals(404, response.get("status")),
		        () -> Assertions.assertEquals("/dummy", response.get("path"))
		);
	}
	
	@Test
	void testParentActuator() throws Exception {
		
		testActuator("/actuator/beans", "Parent actuator response is wrong!");
	}

	@SuppressWarnings("unchecked")
	@Test
	void testChildFirst() throws Exception {

		Map<String, String> response = restTemplate.getForObject("/first/", Map.class);
		
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

		Map<String, ?> response = restTemplate.getForObject("/first/dummy", Map.class);
		
		Assertions.assertAll("Error response for non-existing URL on the first child context is wrong!",
		        () -> Assertions.assertEquals("Not Found", response.get("error")),
		        () -> Assertions.assertEquals(404, response.get("status")),
		        () -> Assertions.assertEquals("/first/dummy", response.get("path"))
		);
	}
	
	@Test
	void testChildFirstActuator() throws Exception {
		
		testActuator("/first/actuator/beans", "The first child actuator response is wrong!");
	}

	@SuppressWarnings("unchecked")
	@Test
	void testChildSecond() throws Exception {

		Map<String, String> response = restTemplate.getForObject("/second/", Map.class);
		
		Assertions.assertAll("Response from the second child context is wrong!",
				() -> Assertions.assertEquals("parent_bean", response.get("parentBean")),
		        () -> Assertions.assertNull(response.get("childFirstBean")),
		        () -> Assertions.assertEquals("child_second_bean", response.get("childSecondBean")),
		        () -> Assertions.assertEquals("common_prop", response.get("parentProperty")),
		        () -> Assertions.assertEquals("null", response.get("childFirstProperty")),
		        () -> Assertions.assertEquals("prop_second", response.get("childSecondProperty"))
		);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void testChildSecondNotExists() throws Exception {

		Map<String, ?> response = restTemplate.getForObject("/second/dummy", Map.class);
		
		Assertions.assertAll("Error response for non-existing URL on the second child context is wrong!",
		        () -> Assertions.assertEquals("Not Found", response.get("error")),
		        () -> Assertions.assertEquals(404, response.get("status")),
		        () -> Assertions.assertEquals("/second/dummy", response.get("path"))
		);
	}
	
	@Test
	void testChildSecondActuator() throws Exception {

		testActuator("/second/actuator/beans", "The second child actuator response is wrong!");
	}
	
	@SuppressWarnings("unchecked")
	void testActuator(String URL, String message) throws Exception {

		Map<String, String> response = restTemplate.getForObject(URL, Map.class);

		Assertions.assertNotNull(response.get("contexts"), message);
	}
}
