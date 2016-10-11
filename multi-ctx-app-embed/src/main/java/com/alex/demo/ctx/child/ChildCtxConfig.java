package com.alex.demo.ctx.child;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @see org.springframework.boot.actuate.autoconfigure.EndpointWebMvcChildContextConfiguration
 */
@Configuration
@EnableWebMvc
@ComponentScan("com.alex.demo.ctx.child")
@Import({ PropertyPlaceholderAutoConfiguration.class, EmbeddedServletContainerAutoConfiguration.class,
		DispatcherServletAutoConfiguration.class })
@PropertySource("classpath:context-child.properties")
public class ChildCtxConfig {

    @Bean(name = "child_bean")
    public String getChildBean() {
        return "child_bean";
    }

    /**
     * If we have parent web context - we need this bean.
     * 
     * @return
     */
    @Profile("parent")
	@Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
	public DispatcherServlet dispatcherServlet() {
		DispatcherServlet dispatcherServlet = new DispatcherServlet();
		// Ensure the parent configuration does not leak down to us
		dispatcherServlet.setDetectAllHandlerAdapters(false);
		dispatcherServlet.setDetectAllHandlerExceptionResolvers(false);
		dispatcherServlet.setDetectAllHandlerMappings(false);
		dispatcherServlet.setDetectAllViewResolvers(false);
		return dispatcherServlet;
	}


    /**
     * If we have parent web context - we need this bean.
     * 
     * @return
     */
    @Profile("parent")
	@Bean(name = DispatcherServlet.HANDLER_MAPPING_BEAN_NAME)
	public CompositeHandlerMapping compositeHandlerMapping() {
		return new CompositeHandlerMapping();
	}


    /**
     * If we have parent web context - we need this bean.
     * 
     * @return
     */
    @Profile("parent")
	@Bean(name = DispatcherServlet.HANDLER_ADAPTER_BEAN_NAME)
	public CompositeHandlerAdapter compositeHandlerAdapter() {
		return new CompositeHandlerAdapter();
	}


    /**
     * If we have parent web context - we need this bean.
     * 
     * @return
     */
    @Profile("parent")
	@Bean(name = DispatcherServlet.HANDLER_EXCEPTION_RESOLVER_BEAN_NAME)
	public CompositeHandlerExceptionResolver compositeHandlerExceptionResolver() {
		return new CompositeHandlerExceptionResolver();
	}

	// ---------------- START SERVER CONFIG

    /**
     * If we have parent web context - we can customize server this way.
     * 
     * @return
     */
    @Profile("parent")
    @Bean
	public ServerCustomization serverCustomization() {
		return new ServerCustomization();
	}


    /**
     * If we don't have parent web context - we have to define {@link ServerProperties}.
     * Even if we have parent web context we can do this way but {@link ServerCustomization} is more
     * flexible.
     * 
     * @return
     */
    @Profile("!parent")
    @Bean
	public ServerProperties serverProperties() {
		ServerProperties serverProperties = new ServerProperties();
		serverProperties.setPort(8082);
		return serverProperties;
	}

	/**
	 * @see org.springframework.boot.actuate.autoconfigure.EndpointWebMvcChildContextConfiguration.ServerCustomization
	 */
	static class ServerCustomization implements EmbeddedServletContainerCustomizer, Ordered {

		@Autowired
		private ListableBeanFactory beanFactory;

		private ServerProperties server;

		@Override
		public int getOrder() {
			return 0;
		}

		@Override
		public void customize(ConfigurableEmbeddedServletContainer container) {
			this.server = BeanFactoryUtils.beanOfTypeIncludingAncestors(this.beanFactory, ServerProperties.class);
			// Customize as per the parent context first (so e.g. the access
			// logs go to the same place)
			this.server.customize(container);
			// Then reset the error pages
			container.setErrorPages(Collections.<ErrorPage> emptySet());
			// and the context path
			container.setContextPath("");
			// and add the management-specific bits
			container.setPort(8082);
			// if (this.managementServerProperties.getSsl() != null) {
			// container.setSsl(this.managementServerProperties.getSsl());
			// }
			container.setServerHeader(this.server.getServerHeader());
			// container.setAddress(this.managementServerProperties.getAddress());
			container.addErrorPages(new ErrorPage(this.server.getError().getPath()));
		}
	}
	// ---------------- END SERVER CONFIG

	/**
	 * @see org.springframework.boot.actuate.autoconfigure.EndpointWebMvcChildContextConfiguration.CompositeHandlerMapping
	 */
	static class CompositeHandlerMapping implements HandlerMapping {

		@Autowired
		private ListableBeanFactory beanFactory;

		private List<HandlerMapping> mappings;

		@Override
		public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
			if (this.mappings == null) {
				this.mappings = extractMappings();
			}
			for (HandlerMapping mapping : this.mappings) {
				HandlerExecutionChain handler = mapping.getHandler(request);
				if (handler != null) {
					return handler;
				}
			}
			return null;
		}

		private List<HandlerMapping> extractMappings() {
			List<HandlerMapping> list = new ArrayList<HandlerMapping>();
			list.addAll(this.beanFactory.getBeansOfType(HandlerMapping.class).values());
			list.remove(this);
			AnnotationAwareOrderComparator.sort(list);
			return list;
		}

	}

	/**
	 * @see org.springframework.boot.actuate.autoconfigure.EndpointWebMvcChildContextConfiguration.CompositeHandlerAdapter
	 */
	static class CompositeHandlerAdapter implements HandlerAdapter {

		@Autowired
		private ListableBeanFactory beanFactory;

		private List<HandlerAdapter> adapters;

		private List<HandlerAdapter> extractAdapters() {
			List<HandlerAdapter> list = new ArrayList<HandlerAdapter>();
			list.addAll(this.beanFactory.getBeansOfType(HandlerAdapter.class).values());
			list.remove(this);
			AnnotationAwareOrderComparator.sort(list);
			return list;
		}

		@Override
		public boolean supports(Object handler) {
			if (this.adapters == null) {
				this.adapters = extractAdapters();
			}
			for (HandlerAdapter mapping : this.adapters) {
				if (mapping.supports(handler)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
				throws Exception {
			if (this.adapters == null) {
				this.adapters = extractAdapters();
			}
			for (HandlerAdapter mapping : this.adapters) {
				if (mapping.supports(handler)) {
					return mapping.handle(request, response, handler);
				}
			}
			return null;
		}

		@Override
		public long getLastModified(HttpServletRequest request, Object handler) {
			if (this.adapters == null) {
				this.adapters = extractAdapters();
			}
			for (HandlerAdapter mapping : this.adapters) {
				if (mapping.supports(handler)) {
					return mapping.getLastModified(request, handler);
				}
			}
			return 0;
		}

	}

	/**
	 * @see org.springframework.boot.actuate.autoconfigure.EndpointWebMvcChildContextConfiguration.CompositeHandlerExceptionResolver
	 */
	static class CompositeHandlerExceptionResolver implements HandlerExceptionResolver {

		@Autowired
		private ListableBeanFactory beanFactory;

		private List<HandlerExceptionResolver> resolvers;

		private List<HandlerExceptionResolver> extractResolvers() {
			List<HandlerExceptionResolver> list = new ArrayList<HandlerExceptionResolver>();
			list.addAll(this.beanFactory.getBeansOfType(HandlerExceptionResolver.class).values());
			list.remove(this);
			AnnotationAwareOrderComparator.sort(list);
			return list;
		}

		@Override
		public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
				Exception ex) {
			if (this.resolvers == null) {
				this.resolvers = extractResolvers();
			}
			for (HandlerExceptionResolver mapping : this.resolvers) {
				ModelAndView mav = mapping.resolveException(request, response, handler, ex);
				if (mav != null) {
					return mav;
				}
			}
			return null;
		}
	}
}
