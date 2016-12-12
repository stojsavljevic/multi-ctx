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

import com.alex.demo.ctx.child.second.ChildSecondCtxConfig;
import com.alex.demo.ctx.parent.ParentCtxConfig;



@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ParentCtxConfig.class,
        ChildSecondCtxConfig.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class ChildSecondCtxControllerTests {

    @SuppressWarnings("unchecked")
    @Test
    public void testChildSecond()
            throws Exception {

        Map<String, String> response = new RestTemplate().getForObject("http://localhost:8081/second", Map.class);

        assertEquals("parent_bean", response.get("parentBean"));
        assertNull(response.get("childFirstBean"));
        assertEquals("child_second_bean", response.get("childSecondBean"));
        assertEquals("common_prop", response.get("parentProperty"));
        assertEquals("null", response.get("childFirstProperty"));
        assertEquals("prop_second", response.get("childSecondProperty"));
    }
}
