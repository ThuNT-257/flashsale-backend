package com.sapotest.flashsale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the Flash Sale application.
 * Enables scheduling to support background inventory cleanup and maintenance tasks.
 */
@SpringBootApplication
@EnableScheduling
public class FlashsaleApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlashsaleApplication.class, args);
	}

}