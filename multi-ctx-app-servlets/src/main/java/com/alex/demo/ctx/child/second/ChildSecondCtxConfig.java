package com.alex.demo.ctx.child.second;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan
@EnableWebMvc
@PropertySource("classpath:context-second.properties")
public class ChildSecondCtxConfig {

    @Bean(name = "child_second_bean")
    public String getChildSecondBean() {
        return "child_second_bean";
    }
}
