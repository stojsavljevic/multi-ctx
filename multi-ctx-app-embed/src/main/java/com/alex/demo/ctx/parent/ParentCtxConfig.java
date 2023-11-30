package com.alex.demo.ctx.parent;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationContextFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import com.alex.demo.ctx.child.ChildCtxConfig;

@SpringBootApplication
public class ParentCtxConfig {

	@Autowired
	Environment environment;

	@Bean(name = "parent_bean")
	String getParentBean() {
		return "parent_bean";
	}

	/**
	 * @see org.springframework.boot.actuate.autoconfigure.web.server.ChildManagementContextInitializer#createManagementContext()
	 * @param parentContext
	 * @return
	 */
	@Bean
	ConfigurableApplicationContext createManagementContext(ApplicationContext parentContext) {

		AnnotationConfigServletWebServerApplicationContext childContext = (AnnotationConfigServletWebServerApplicationContext) ApplicationContextFactory.DEFAULT
				.create(WebApplicationType.SERVLET);
		ConfigurableEnvironment childEnvironment = ApplicationContextFactory.DEFAULT
				.createEnvironment(WebApplicationType.SERVLET);
		childContext.setEnvironment(childEnvironment);

		if (parentContext.getEnvironment() instanceof ConfigurableEnvironment configurableEnvironment) {
			childEnvironment.setConversionService((configurableEnvironment).getConversionService());
		}

		if (hasProfile("parent")) {
			childContext.setParent(parentContext);
		}

		childContext.setServerNamespace("first");
		childContext.setClassLoader(parentContext.getClassLoader());

		childContext.setNamespace("first");
		childContext.setId(parentContext.getId() + ":first");

		childContext.register(ChildCtxConfig.class);
		childContext.refresh();

		return childContext;
	}

	boolean hasProfile(String profileName) {
		return Arrays.stream(environment.getActiveProfiles()).anyMatch(profile -> profile.equals(profileName));
	}
}
