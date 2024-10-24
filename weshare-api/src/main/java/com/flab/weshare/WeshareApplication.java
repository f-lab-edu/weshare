package com.flab.weshare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@EntityScan("com.flab.core")
@EnableJpaRepositories("com.flab.core")
@EnableCaching
@Import({
	com.flab.mail.mail.service.MailPublisher.class
})
@SpringBootApplication(scanBasePackages = {
	"com.flab.weshare",
	"com.flab.core",
	"com.flab.wesharepay"
})
public class WeshareApplication {
	public static void main(String[] args) {
		SpringApplication.run(WeshareApplication.class, args);
	}
}

