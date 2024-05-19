package com.flab.weshare;

import org.springframework.boot.SpringApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@SpringBootApplicatio
public class WeshareApplication {
	public static void main(String[] args) {
		SpringApplication.run(WeshareApplication.class, args);
	}
}
