## Spring Boot Multi Context Application using AnnotationConfigEmbeddedWebApplicationContext

This sample application demonstrates creation of three contexts:

1. Parent web context [http://localhost:8080/parent](http://localhost:8080/parent)
2. Child web context on separate port [http://localhost:8082/child](http://localhost:8082/child)
3. Actuator (child) context on separate port [http://localhost:8081/actuator/beans](http://localhost:8081/actuator/beans)

Child context is type `org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext`.
It is created using Java configuration:
```
@Bean
public AnnotationConfigServletWebServerApplicationContext createChildContext(ApplicationContext parentContext) {
  AnnotationConfigServletWebServerApplicationContext childContext = new AnnotationConfigServletWebServerApplicationContext();
  ...
  return childContext;
}
```

It's exposed on port 8082 while actuator is exposed on port 8081.
Most of the logic used for child context creation is taken from: `org.springframework.boot.actuate.autoconfigure.web.servlet.WebMvcEndpointChildContextConfiguration`.
So, child and actuator contexts are very similar.

This demo application provides two options:
* child context is standalone (not child of parent web context). This is default.
* child context is child of parent context. In order to get this behavior application should be started with *parent* Spring profile (e.g. with startup parameter `--spring.profiles.active=parent`)
