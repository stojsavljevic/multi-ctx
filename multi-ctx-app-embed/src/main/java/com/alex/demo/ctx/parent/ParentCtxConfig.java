package com.alex.demo.ctx.parent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import com.alex.demo.ctx.child.ChildCtxConfig;

@SpringBootApplication
public class ParentCtxConfig {

	@Autowired
	Environment environment;

	@Bean(name = "parent_bean")
	public String getParentBean() {
		return "parent_bean";
	}

	/**
	 * @see org.springframework.boot.actuate.autoconfigure.web.servlet.ServletManagementContextFactory#createManagementContext(ApplicationContext, Class...)
	 * @param parentContext
	 * @return
	 */
	@Bean
	public AnnotationConfigServletWebServerApplicationContext createChildContext(ApplicationContext parentContext) {
		AnnotationConfigServletWebServerApplicationContext childContext = new AnnotationConfigServletWebServerApplicationContext();

		if (hasProfile("parent")) {
			childContext.setParent(parentContext);
		}

		childContext.setNamespace("first");
		childContext.setId(parentContext.getId() + ":first");

		childContext.register(ChildCtxConfig.class);
		childContext.refresh();

		return childContext;
	}

	boolean hasProfile(String profileName) {
		for (String profile : environment.getActiveProfiles()) {
			if (profile.equals(profileName)) {
				return true;
			}
		}
		return false;
	}
}
