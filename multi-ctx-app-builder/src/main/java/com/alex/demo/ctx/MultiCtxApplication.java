package com.alex.demo.ctx;


import org.springframework.boot.builder.SpringApplicationBuilder;

import com.alex.demo.ctx.child.first.ChildFirstCtxConfig;
import com.alex.demo.ctx.child.second.ChildSecondCtxConfig;
import com.alex.demo.ctx.parent.ParentCtxConfig;



public class MultiCtxApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(ParentCtxConfig.class)
                .child(ChildFirstCtxConfig.class).sibling(ChildSecondCtxConfig.class).run(args);
    }
}
