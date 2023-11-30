package com.alex.demo.ctx.parent;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
public class ParentCtxConfig {

	@Bean(name = "parent_bean")
	String getParentBean() {
		return "parent_bean";
	}
}
