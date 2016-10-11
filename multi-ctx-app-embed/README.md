## Spring Boot Multi Context Application using AnnotationConfigEmbeddedWebApplicationContext

This sample application demonstrates creation of three contexts:

1. Parent web context [http://localhost:8080/parent](http://localhost:8080/parent)
2. Child web context on separate port [http://localhost:8082/child](http://localhost:8082/child)
3. Actuator (child) context on separate port [http://localhost:8081/beans](http://localhost:8081/beans)

Child context is type `org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext` and it is created using Java configuration:
```
@Bean
public AnnotationConfigEmbeddedWebApplicationContext createChildContext(ApplicationContext parentContext) {
  AnnotationConfigEmbeddedWebApplicationContext childContext = new AnnotationConfigEmbeddedWebApplicationContext();
  ...
  return childContext;
}
```

Child context is exposed on port 8082 while actuator is exposed on port 8081.
Most of the logic used for child context creation is taken from: `org.springframework.boot.actuate.autoconfigure.EndpointWebMvcAutoConfiguration` and `org.springframework.boot.actuate.autoconfigure.EndpointWebMvcChildContextConfiguration`.
So, child and actuator contexts are very similar.

This demo application provides two options:
* child context is standalone (not child of parent web context). This is default.
* child context is child of parent context. In order to get this behavior application should be started with *parent* Spring profile.
