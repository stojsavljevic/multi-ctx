package com.alex.demo.ctx.child;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.servlet.ManagementErrorEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @see org.springframework.boot.actuate.autoconfigure.web.servlet.WebMvcEndpointChildContextConfiguration
 */
@SpringBootConfiguration
@EnableWebMvc
@ComponentScan("com.alex.demo.ctx.child")
@Import({ PropertyPlaceholderAutoConfiguration.class, ServletWebServerFactoryAutoConfiguration.class, DispatcherServletAutoConfiguration.class })
@PropertySource("classpath:context-child.properties")
public class ChildCtxConfig {

	@Bean(name = "child_bean")
	public String getChildBean() {
		return "child_bean";
	}
	
	@Value("${server.port:8888}")
	int parentProperty;

	@Bean
	@ConditionalOnMissingBean(value = ErrorAttributes.class, search = SearchStrategy.CURRENT)
	public DefaultErrorAttributes errorAttributes() {
		return new DefaultErrorAttributes();
	}

	@Bean
	@ConditionalOnBean(ErrorAttributes.class)
	public ManagementErrorEndpoint errorEndpoint(ErrorAttributes errorAttributes) {
		return new ManagementErrorEndpoint(errorAttributes, new ErrorProperties());
	}

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

	@Profile("parent")
	@Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME)
	public DispatcherServletRegistrationBean dispatcherServletRegistrationBean(DispatcherServlet dispatcherServlet) {
		return new DispatcherServletRegistrationBean(dispatcherServlet, "/");
	}

	@Profile("parent")
	@Bean(name = DispatcherServlet.HANDLER_MAPPING_BEAN_NAME)
	public CompositeHandlerMapping compositeHandlerMapping() {
		return new CompositeHandlerMapping();
	}

	@Profile("parent")
	@Bean(name = DispatcherServlet.HANDLER_ADAPTER_BEAN_NAME)
	public CompositeHandlerAdapter compositeHandlerAdapter(ListableBeanFactory beanFactory) {
		return new CompositeHandlerAdapter(beanFactory);
	}

	@Profile("parent")
	@Bean
	@ConditionalOnMissingBean({ RequestContextListener.class, RequestContextFilter.class })
	public RequestContextFilter requestContextFilter() {
		return new OrderedRequestContextFilter();
	}

	@Component
	public class MyTomcatWebServerCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

		@Autowired
		private ListableBeanFactory beanFactory;

		@Override
		public void customize(TomcatServletWebServerFactory factory) {
			ServerProperties server = BeanFactoryUtils.beanOfTypeIncludingAncestors(this.beanFactory,
					ServerProperties.class);

			factory.setPort(parentProperty);
			factory.addErrorPages(new ErrorPage(server.getError().getPath()));
			factory.setServerHeader(server.getServerHeader());
		}
	}

	/**
	 * @see org.springframework.boot.actuate.autoconfigure.web.servlet.CompositeHandlerMapping
	 */
	class CompositeHandlerMapping implements HandlerMapping {

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
		    List<HandlerMapping> list = new ArrayList<>(this.beanFactory.getBeansOfType(HandlerMapping.class).values());
			list.remove(this);
			AnnotationAwareOrderComparator.sort(list);
			return list;
		}
	}

	/**
	 * @see org.springframework.boot.actuate.autoconfigure.web.servlet.CompositeHandlerAdapter
	 */
	class CompositeHandlerAdapter implements HandlerAdapter {

		private final ListableBeanFactory beanFactory;

		private List<HandlerAdapter> adapters;

		CompositeHandlerAdapter(ListableBeanFactory beanFactory) {
			this.beanFactory = beanFactory;
		}

		@Override
		public boolean supports(Object handler) {
			return getAdapter(handler).isPresent();
		}

		@Override
		public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
				throws Exception {
			Optional<HandlerAdapter> adapter = getAdapter(handler);
			if (adapter.isPresent()) {
				return adapter.get().handle(request, response, handler);
			}
			return null;
		}

		@Override
		public long getLastModified(HttpServletRequest request, Object handler) {
		    Optional<HandlerAdapter> adapter = getAdapter(handler);
	        return adapter.map((handlerAdapter) -> handlerAdapter.getLastModified(request, handler)).orElse(0L);
		}

		private Optional<HandlerAdapter> getAdapter(Object handler) {
			if (this.adapters == null) {
				this.adapters = extractAdapters();
			}
			return this.adapters.stream().filter((a) -> a.supports(handler)).findFirst();
		}

		private List<HandlerAdapter> extractAdapters() {
		    List<HandlerAdapter> list = new ArrayList<>(this.beanFactory.getBeansOfType(HandlerAdapter.class).values());
	        list.remove(this);
	        AnnotationAwareOrderComparator.sort(list);
	        return list;
		}
	}
}
