## Multi Context Application

******************
**IMPORTANT NOTICE**

After upgrading to Spring Boot `1.5.1.RELEASE` first module (`multi-ctx-app-builder`) is giving the error on startup:

```
org.springframework.jmx.export.UnableToRegisterMBeanException: Unable to register MBean [org.springframework.boot.actuate.endpoint.jmx.AuditEventsJmxEndpoint@30d12fd8] with key 'auditEventsEndpoint'; nested exception is javax.management.InstanceAlreadyExistsException: org.springframework.boot:type=Endpoint,name=auditEventsEndpoint
  at org.springframework.jmx.export.MBeanExporter.registerBeanNameOrInstance(MBeanExporter.java:628) ~[spring-context-4.3.6.RELEASE.jar:4.3.6.RELEASE]
  ...
```
Still, module is running fine.

Also, Actuator's _/beans_ endpoints in all modules are returning an empty array.

Everything works as expected with `1.4.3.RELEASE`.
But `1.5` is required because of `@ContextHierarchy` support: https://github.com/spring-projects/spring-boot/issues/8000
******************

This sample application(s) uses Spring Boot and handles multiple contexts inside one application. Samples handle contexts in different ways:

1. [Using SpringApplicationBuilder] (https://github.com/stojsavljevic/multi-ctx/tree/master/multi-ctx-app-builder)
2. [Using AnnotationConfigEmbeddedWebApplicationContext] (https://github.com/stojsavljevic/multi-ctx/tree/master/multi-ctx-app-embed)
3. [Using ServletRegistrationBean] (https://github.com/stojsavljevic/multi-ctx/tree/master/multi-ctx-app-servlets)


All of the samples have actuator turned on. Contexts and their beans can be checked using actuator's _/beans_ endpoint.
