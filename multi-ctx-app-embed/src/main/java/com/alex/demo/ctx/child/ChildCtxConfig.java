package com.alex.demo.ctx.child;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.servlet.ManagementErrorEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
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
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
	String getChildBean() {
		return "child_bean";
	}
	
	@Value("${child.server.port}")
	int childContextServerPort;

	@Bean
	@ConditionalOnMissingBean(value = ErrorAttributes.class, search = SearchStrategy.CURRENT)
	DefaultErrorAttributes errorAttributes() {
		return new DefaultErrorAttributes();
	}

	@Bean
	@ConditionalOnBean(ErrorAttributes.class)
	ManagementErrorEndpoint errorEndpoint(ErrorAttributes errorAttributes, ServerProperties serverProperties) {
		return new ManagementErrorEndpoint(errorAttributes, serverProperties.getError());
	}

	@Profile("parent")
	@Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
	DispatcherServlet dispatcherServlet() {
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
	DispatcherServletRegistrationBean dispatcherServletRegistrationBean(DispatcherServlet dispatcherServlet) {
		return new DispatcherServletRegistrationBean(dispatcherServlet, "/");
	}

	@Profile("parent")
	@Bean(name = DispatcherServlet.HANDLER_MAPPING_BEAN_NAME)
	CompositeHandlerMapping compositeHandlerMapping() {
		return new CompositeHandlerMapping();
	}

	@Profile("parent")
	@Bean(name = DispatcherServlet.HANDLER_ADAPTER_BEAN_NAME)
	CompositeHandlerAdapter compositeHandlerAdapter(ListableBeanFactory beanFactory) {
		return new CompositeHandlerAdapter(beanFactory);
	}
	
	@Profile("parent")
	@Bean(name = DispatcherServlet.HANDLER_EXCEPTION_RESOLVER_BEAN_NAME)
	CompositeHandlerExceptionResolver compositeHandlerExceptionResolver() {
		return new CompositeHandlerExceptionResolver();
	}

	@Profile("parent")
	@Bean
	@ConditionalOnMissingBean({ RequestContextListener.class, RequestContextFilter.class })
	RequestContextFilter requestContextFilter() {
		return new OrderedRequestContextFilter();
	}

	@Component
	class MyTomcatWebServerCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

		@Autowired
		private ListableBeanFactory beanFactory;

		@Override
		public void customize(TomcatServletWebServerFactory factory) {
			ServerProperties server = BeanFactoryUtils.beanOfTypeIncludingAncestors(this.beanFactory,
					ServerProperties.class);

			factory.setPort(childContextServerPort);
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
			for (HandlerMapping mapping : getMappings()) {
				HandlerExecutionChain handler = mapping.getHandler(request);
				if (handler != null) {
					return handler;
				}
			}
			return null;
		}

		@Override
		public boolean usesPathPatterns() {
			for (HandlerMapping mapping : getMappings()) {
				if (mapping.usesPathPatterns()) {
					return true;
				}
			}
			return false;
		}

		private List<HandlerMapping> getMappings() {
			if (this.mappings == null) {
				this.mappings = extractMappings();
			}
			return this.mappings;
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
		@Deprecated(since = "2.4.9", forRemoval = false)
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
	
	/**
	 * @see org.springframework.boot.actuate.autoconfigure.web.servlet.CompositeHandlerExceptionResolver
	 */
	class CompositeHandlerExceptionResolver implements HandlerExceptionResolver {

		@Autowired
		private ListableBeanFactory beanFactory;

		private volatile List<HandlerExceptionResolver> resolvers;

		@Override
		public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
				Exception ex) {
			for (HandlerExceptionResolver resolver : getResolvers()) {
				ModelAndView resolved = resolver.resolveException(request, response, handler, ex);
				if (resolved != null) {
					return resolved;
				}
			}
			return null;
		}

		private List<HandlerExceptionResolver> getResolvers() {
			List<HandlerExceptionResolver> resolvers = this.resolvers;
			if (resolvers == null) {
				resolvers = new ArrayList<>();
				collectResolverBeans(resolvers, this.beanFactory);
				resolvers.remove(this);
				AnnotationAwareOrderComparator.sort(resolvers);
				if (resolvers.isEmpty()) {
					resolvers.add(new DefaultErrorAttributes());
					resolvers.add(new DefaultHandlerExceptionResolver());
				}
				this.resolvers = resolvers;
			}
			return resolvers;
		}

		private void collectResolverBeans(List<HandlerExceptionResolver> resolvers, BeanFactory beanFactory) {
			if (beanFactory instanceof ListableBeanFactory listableBeanFactory) {
				resolvers.addAll(listableBeanFactory.getBeansOfType(HandlerExceptionResolver.class).values());
			}
			if (beanFactory instanceof HierarchicalBeanFactory hierarchicalBeanFactory) {
				collectResolverBeans(resolvers, hierarchicalBeanFactory.getParentBeanFactory());
			}
		}
	}
}
