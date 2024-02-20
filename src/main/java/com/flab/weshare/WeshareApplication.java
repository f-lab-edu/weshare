package com.flab.weshare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class WeshareApplication {
	public static void main(String[] args) {
		SpringApplication.run(WeshareApplication.class, args);
	}
}
