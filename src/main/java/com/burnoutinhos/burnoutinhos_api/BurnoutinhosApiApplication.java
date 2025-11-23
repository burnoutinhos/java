package com.burnoutinhos.burnoutinhos_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BurnoutinhosApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BurnoutinhosApiApplication.class, args);
	}

}
