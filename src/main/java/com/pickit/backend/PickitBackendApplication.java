package com.pickit.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.data.jpa.repository.config.EnableJpaAuditing
public class PickitBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PickitBackendApplication.class, args);
	}

}
