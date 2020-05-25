package com.alex.demo.ctx.parent;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParentCtxConfig {

	@Bean(name = "parent_bean")
	public String getParentBean() {
		return "parent_bean";
	}
}
