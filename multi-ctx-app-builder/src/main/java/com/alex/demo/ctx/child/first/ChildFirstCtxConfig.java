package com.alex.demo.ctx.child.first;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:context-first.properties")
public class ChildFirstCtxConfig {

    @Bean(name = "child_first_bean")
    public String getChildFirstBean() {
        return "child_first_bean";
    }
}
