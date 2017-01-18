package com.alex.demo.ctx.child.first;


import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;



@SpringBootConfiguration
@ComponentScan
@EnableWebMvc
@PropertySource("classpath:context-first.properties")
public class ChildFirstCtxConfig {

    @Bean(name = "child_first_bean")
    public String getChildFirstBean() {
        return "child_first_bean";
    }
}
