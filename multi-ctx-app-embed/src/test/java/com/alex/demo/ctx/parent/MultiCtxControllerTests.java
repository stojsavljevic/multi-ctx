package com.alex.demo.ctx.parent;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MultiCtxControllerTests extends CommonTest {

	@SuppressWarnings("unchecked")
	@Test
	void testChild() throws Exception {
		
		Map<String, String> response = restTemplate.getForObject("http://localhost:{childPort}/child", Map.class, getChildPort());
		
		Assertions.assertAll("Response from child context is wrong!",
		        () -> Assertions.assertNull(response.get("parentBean")),
		        () -> Assertions.assertEquals("child_bean", response.get("childBean")),
		        () -> Assertions.assertEquals("null", response.get("parentProperty")),
		        () -> Assertions.assertEquals("prop_child", response.get("childProperty"))
		);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void testChildNotExists() throws Exception {

		Map<String, ?> response = restTemplate.getForObject("http://localhost:{childPort}/child/dummy", Map.class, getChildPort());

		Assertions.assertAll("Error response for non-existing URL on child context is wrong!",
		        () -> Assertions.assertEquals("Not Found", response.get("error")),
		        () -> Assertions.assertEquals(404, response.get("status")),
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
		        () -> Assertions.assertEquals("parent_prop", response.get("parentProperty")),
		        () -> Assertions.assertEquals("null", response.get("childProperty"))
		);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void testParentNotExists() throws Exception {

		Map<String, ?> response = restTemplate.getForObject("/parent/dummy", Map.class);

		Assertions.assertAll("Error response for non-existing URL on parent context is wrong!",
		        () -> Assertions.assertEquals("Not Found", response.get("error")),
		        () -> Assertions.assertEquals(404, response.get("status")),
		        () -> Assertions.assertEquals("/parent/dummy", response.get("path"))
		);
	}
}
