# Multi Context Application

This sample application(s) uses Spring Boot and handles multiple contexts inside one application. Samples handle contexts in different ways:

1. [Using SpringApplicationBuilder] (https://github.com/stojsavljevic/multi-ctx/tree/master/multi-ctx-app-builder)
2. [Using AnnotationConfigEmbeddedWebApplicationContext] (https://github.com/stojsavljevic/multi-ctx/tree/master/multi-ctx-app-embed)
3. [Using ServletRegistrationBean] (https://github.com/stojsavljevic/multi-ctx/tree/master/multi-ctx-app-servlets)


All of the samples have actuator turned on. Contexts and their beans can be checked using actuator's _/beans_ endpoint.


## Known issues

1. `UnableToRegisterMBeanException` on startup of first module (`multi-ctx-app-builder`) . Still, module is running fine. [GitHub issue](https://github.com/spring-projects/spring-boot/issues/8152).

2. Actuator's _/beans_ endpoints in all modules are returning an empty array if application is run from STS. [GitHub issue](https://github.com/spring-projects/spring-boot/issues/8146).

Issues are related to Spring Boot `1.5.1.RELEASE`. Everything works fine with `1.4.3.RELEASE` but there is no `@ContextHierarchy` [support](https://github.com/spring-projects/spring-boot/issues/8000) in tests.