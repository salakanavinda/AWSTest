/*
 * Copyright (c) Virtusa Corporation 2018. All Rights Reserved.<br><br>
 *
 * BT Innovation Hub (BTASMiHub@virtusa.com)
 */
package com.ihub.asm360.adaptor.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * The Class FeedAdaptorApplication.
 */
@ComponentScan({ "com.ihub.asm360.adaptor.core.service", "com.ihub.asm360.adaptor.core",
		"com.ihub.asm360.adaptor.app.config", "com.ihub.asm360.adaptor.app" , "com.ihub.asm360.adaptor.app.handler" })
@EntityScan("com.ihub.asm360.adaptor.core.entity")
@EnableJpaRepositories("com.ihub.asm360.adaptor.core.repository")
@SpringBootApplication
@EnableAutoConfiguration
public class FeedAdaptorApplication {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(FeedAdaptorApplication.class, args);
		AppBanner.printBanner();
	}

}
