# Multi Context Application  [![Build Status](https://travis-ci.org/stojsavljevic/multi-ctx.svg?branch=master)](https://travis-ci.org/stojsavljevic/multi-ctx)

This sample application(s) uses Spring Boot and handles multiple contexts inside one application. Samples handle contexts in different ways:

1. [Using SpringApplicationBuilder](https://github.com/stojsavljevic/multi-ctx/tree/master/multi-ctx-app-builder)
2. [Using AnnotationConfigServletWebServerApplicationContext](https://github.com/stojsavljevic/multi-ctx/tree/master/multi-ctx-app-embed)
3. [Using ServletRegistrationBean](https://github.com/stojsavljevic/multi-ctx/tree/master/multi-ctx-app-servlets)


All of the samples have actuator turned on. Contexts and their beans can be checked using actuator's _/beans_ endpoint.
