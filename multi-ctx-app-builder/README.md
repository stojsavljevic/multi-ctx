## Spring Boot Multi Context Application using SpringApplicationBuilder

This sample application demonstrates creation of three contexts:

1. Parent application context
2. Child web context #1 on separate port [http://localhost:8080/first/](http://localhost:8080/first/)
3. Child web context #2 on separate port [http://localhost:8081/second/](http://localhost:8081/second/)

Contexts are created using Spring Boot's [Fluent builder API](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.spring-application.fluent-builder-api):

```
public static void main(String[] args) {
    new SpringApplicationBuilder().sources(ParentCtxConfig.class)
            .child(ChildFirstCtxConfig.class).sibling(ChildSecondCtxConfig.class).run(args);
}
```

Child contexts are exposed on different ports - 8080 and 8081 respectively.

Actuator is turned on in both web contexts. Example endpoints:
* [http://localhost:8080/first/actuator/beans](http://localhost:8080/first/actuator/beans)
* [http://localhost:8081/second/actuator/beans](http://localhost:8081/second/actuator/beans)


In case you're getting exception like this on startup:

```
javax.management.InstanceAlreadyExistsException: org.springframework.boot:type=Admin,name=SpringApplication

```
either disable JMX in your IDE or uncomment `spring.application.admin.jmx-name` property in `context-first.properties` file.
