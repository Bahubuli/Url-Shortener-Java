package com.url.shortener.Vyson;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling // Enable scheduling for @Scheduled to work
public class VysonApplication {

	public static void main(String[] args) {
		SpringApplication.run(VysonApplication.class, args);
	}

}
