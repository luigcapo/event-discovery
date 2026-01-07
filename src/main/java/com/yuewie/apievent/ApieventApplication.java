package com.yuewie.apievent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApieventApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApieventApplication.class, args);
	}

}
