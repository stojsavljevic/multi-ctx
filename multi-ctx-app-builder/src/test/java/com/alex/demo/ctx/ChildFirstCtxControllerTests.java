package com.alex.demo.ctx;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.alex.demo.ctx.child.first.ChildFirstCtxConfig;
import com.alex.demo.ctx.parent.ParentCtxConfig;



@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ParentCtxConfig.class,
        ChildFirstCtxConfig.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class ChildFirstCtxControllerTests {

    @SuppressWarnings("unchecked")
    @Test
    public void testChildFirst()
            throws Exception {

        Map<String, String> response = new RestTemplate().getForObject("http://localhost:8080/first", Map.class);

        assertEquals("parent_bean", response.get("parentBean"));
        assertEquals("child_first_bean", response.get("childFirstBean"));
        assertNull(response.get("childSecondBean"));
        assertEquals("common_prop", response.get("parentProperty"));
        assertEquals("prop_first", response.get("childFirstProperty"));
        assertEquals("null", response.get("childSecondProperty"));
    }
}
