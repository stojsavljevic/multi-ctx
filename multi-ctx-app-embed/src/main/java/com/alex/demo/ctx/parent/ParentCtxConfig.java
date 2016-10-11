package com.alex.demo.ctx.parent;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import com.alex.demo.ctx.child.ChildCtxConfig;

@SpringBootApplication(scanBasePackages = { "com.alex.demo.ctx.parent" })
public class ParentCtxConfig {

    @Autowired
    Environment environment;

    @Bean(name = "parent_bean")
    public String getParentBean() {
        return "parent_bean";
    }

	/**
	 * @see org.springframework.boot.actuate.autoconfigure.EndpointWebMvcAutoConfiguration#createChildManagementContext()
	 * @param parentContext
	 * @return
	 */
	@Bean
	public AnnotationConfigEmbeddedWebApplicationContext createChildContext(ApplicationContext parentContext) {
		AnnotationConfigEmbeddedWebApplicationContext childContext = new AnnotationConfigEmbeddedWebApplicationContext();

		// DO WE WANT PARENT CONTEXT!?
        if (hasProfile("parent")) {
            childContext.setParent(parentContext);
        }

		childContext.setNamespace("first");
		childContext.setId(parentContext.getId() + ":first");

		// DON'T KNOW IF WE NEED THIS:
		// context.setClassLoader(parent.getClassLoader());

		// DON'T KNOW IF WE NEED THIS:
		// @see
		// EndpointWebMvcAutoConfiguration#registerEmbeddedServletContainerFactory()
		// registerEmbeddedServletContainerFactory(context, parent);

		childContext.register(ChildCtxConfig.class);
		childContext.refresh();

		return childContext;
	}


    boolean hasProfile(@NotNull String profileName) {
        for (String profile : environment.getActiveProfiles()) {
            if (profileName.equals(profile)) {
                return true;
            }
        }
        return false;
    }
}
