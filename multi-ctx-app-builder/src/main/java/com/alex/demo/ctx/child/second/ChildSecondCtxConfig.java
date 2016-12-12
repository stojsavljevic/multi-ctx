package com.alex.demo.ctx.child.second;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;



@SpringBootApplication
@PropertySource("classpath:context-second.properties")
public class ChildSecondCtxConfig {

    @Bean(name = "child_second_bean")
    public String getChildSecondBean() {
        return "child_second_bean";
    }
}
