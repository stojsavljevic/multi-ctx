## Multi Context Application

******************
**IMPORTANT NOTICE**

After upgrading to Spring Boot `1.5.1.RELEASE` I noticed two issues:

1. `UnableToRegisterMBeanException` on startup of first module (`multi-ctx-app-builder`) . Still, module is running fine. [GitHub issue](https://github.com/spring-projects/spring-boot/issues/8152).

2. Actuator's _/beans_ endpoints in all modules are returning an empty array if application is run from STS. [GitHub issue](https://github.com/spring-projects/spring-boot/issues/8146).


Everything works fine with `1.4.3.RELEASE`.
But `1.5` is required because of `@ContextHierarchy` [support](https://github.com/spring-projects/spring-boot/issues/8000)
******************

This sample application(s) uses Spring Boot and handles multiple contexts inside one application. Samples handle contexts in different ways:

1. [Using SpringApplicationBuilder] (https://github.com/stojsavljevic/multi-ctx/tree/master/multi-ctx-app-builder)
2. [Using AnnotationConfigEmbeddedWebApplicationContext] (https://github.com/stojsavljevic/multi-ctx/tree/master/multi-ctx-app-embed)
3. [Using ServletRegistrationBean] (https://github.com/stojsavljevic/multi-ctx/tree/master/multi-ctx-app-servlets)


All of the samples have actuator turned on. Contexts and their beans can be checked using actuator's _/beans_ endpoint.
