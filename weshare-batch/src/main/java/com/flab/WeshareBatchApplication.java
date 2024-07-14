package com.flab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@EntityScan("com.flab.core")
@EnableJpaRepositories("com.flab.core")
@SpringBootApplication(scanBasePackages = {"com.flab"})
public class WeshareBatchApplication {
	public static void main(String[] args) {
		SpringApplication.run(WeshareBatchApplication.class, args);
	}
}
