package com.alex.demo.ctx.parent;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.alex.demo.ctx.child.first.ChildFirstCtxConfig;
import com.alex.demo.ctx.child.second.ChildSecondCtxConfig;

@SpringBootApplication
public class ParentCtxConfig {

	@Bean(name = "parent_bean")
	String getParentBean() {
		return "parent_bean";
	}

	@Bean
	ServletRegistrationBean<DispatcherServlet> createChildFirstCtx() {
		DispatcherServlet dispatcherServlet = new DispatcherServlet();

		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.register(ChildFirstCtxConfig.class);
		applicationContext.setId("childFirst");

		dispatcherServlet.setApplicationContext(applicationContext);
		ServletRegistrationBean<DispatcherServlet> servletRegistrationBean = new ServletRegistrationBean<>(dispatcherServlet, "/first/*");
		servletRegistrationBean.setName("childFirst");
		servletRegistrationBean.setLoadOnStartup(1);

		return servletRegistrationBean;
	}

	@Bean
	ServletRegistrationBean<DispatcherServlet> createChildSecondCtx() {
		DispatcherServlet dispatcherServlet = new DispatcherServlet();

		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.register(ChildSecondCtxConfig.class);
		applicationContext.setId("childSecond");

		dispatcherServlet.setApplicationContext(applicationContext);
		ServletRegistrationBean<DispatcherServlet> servletRegistrationBean = new ServletRegistrationBean<>(dispatcherServlet, "/second/*");
		servletRegistrationBean.setName("childSecond");
		servletRegistrationBean.setLoadOnStartup(2);

		return servletRegistrationBean;
	}
}
