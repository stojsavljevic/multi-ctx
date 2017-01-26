package com.alex.demo.ctx;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;

import com.alex.demo.ctx.parent.ParentCtxConfig;

@ContextConfiguration(name = "parent", classes = ParentCtxConfig.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE, properties = "spring.main.banner-mode=off")
public class ParentCtxDefinition {

}
