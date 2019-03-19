## Spring Boot Multi Context Application using ServletRegistrationBean

This sample application demonstrates creation of three contexts:

1. Parent web context [http://localhost:8080/](http://localhost:8080/)
2. Child web context #1 on same port as parent [http://localhost:8080/first/](http://localhost:8080/first/)
3. Child web context #2 on same port as parent [http://localhost:8080/second/](http://localhost:8080/second/)

Child context is type `org.springframework.web.context.support.AnnotationConfigWebApplicationContext` and it is created using Java configuration:
```
@Bean
public ServletRegistrationBean createChildFirstCtx() {
    DispatcherServlet dispatcherServlet = new DispatcherServlet();

    AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
    ...
    dispatcherServlet.setApplicationContext(applicationContext);
    ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(dispatcherServlet, "/first/*");
    ...
    return servletRegistrationBean;
}
```

Child contexts are exposed on same port as parent - 8080.

Actuator is turned on in all contexts. Example endpoints:
* [http://localhost:8080/actuator/beans](http://localhost:8080/actuator/beans)
* [http://localhost:8080/first/actuator/beans](http://localhost:8080/first/actuator/beans)
* [http://localhost:8080/second/actuator/beans](http://localhost:8080/second/actuator/beans)
