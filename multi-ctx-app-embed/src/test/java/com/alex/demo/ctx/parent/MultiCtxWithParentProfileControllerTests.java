package com.alex.demo.ctx.parent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("parent")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class MultiCtxWithParentProfileControllerTests {

	@Autowired
	TestRestTemplate restTemplate;

	@Autowired
	ApplicationContext applicationContext;

	@SuppressWarnings("unchecked")
	@Test
	public void testChildWithParent() throws Exception {

		Map<String, String> response = restTemplate.getForObject("http://localhost:{port}/child", Map.class,
				getChildPort());

		assertEquals("parent_bean", response.get("parentBean"));
		assertEquals("child_bean", response.get("childBean"));
		assertEquals("common_prop", response.get("parentProperty"));
		assertEquals("prop_child", response.get("childProperty"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testParent() throws Exception {

		Map<String, String> response = restTemplate.getForObject("/parent", Map.class);

		assertEquals("parent_bean", response.get("parentBean"));
		assertNull(response.get("childBean"));
		assertEquals("common_prop", response.get("parentProperty"));
		assertEquals("null", response.get("childProperty"));
	}

	int getChildPort() {

		AnnotationConfigEmbeddedWebApplicationContext childContext = applicationContext
				.getBean(AnnotationConfigEmbeddedWebApplicationContext.class);
		return childContext.getEmbeddedServletContainer().getPort();
	}
}
