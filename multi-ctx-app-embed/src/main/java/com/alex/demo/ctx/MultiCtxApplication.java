package com.alex.demo.ctx;

import org.springframework.boot.SpringApplication;

import com.alex.demo.ctx.parent.ParentCtxConfig;

public class MultiCtxApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParentCtxConfig.class, args);
	}
}
