package com.alex.demo.ctx.parent;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.alex.demo.ctx.child.first.ChildFirstCtxConfig;
import com.alex.demo.ctx.child.second.ChildSecondCtxConfig;

@SpringBootApplication
@EnableWebMvc
public class ParentCtxConfig {

	@Bean(name = "parent_bean")
	public String getParentBean() {
		return "parent_bean";
	}

	@Bean
	public ServletRegistrationBean createChildFirstCtx() {
		DispatcherServlet dispatcherServlet = new DispatcherServlet();

		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.register(ChildFirstCtxConfig.class);
		applicationContext.setId("childFirst");

		dispatcherServlet.setApplicationContext(applicationContext);
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(dispatcherServlet, "/first/*");
		servletRegistrationBean.setName("childFirst");
		servletRegistrationBean.setLoadOnStartup(1);

		return servletRegistrationBean;
	}

	@Bean
	public ServletRegistrationBean createChildSecondCtx() {
		DispatcherServlet dispatcherServlet = new DispatcherServlet();

		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.register(ChildSecondCtxConfig.class);
		applicationContext.setId("childSecond");

		dispatcherServlet.setApplicationContext(applicationContext);
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(dispatcherServlet, "/second/*");
		servletRegistrationBean.setName("childSecond");
		servletRegistrationBean.setLoadOnStartup(2);

		return servletRegistrationBean;
	}
}
