package com.flab.weshare.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityUtilConfiguration {
	@Value("${aes.password}")
	private String password;

	@Value("${aes.salt}")
	private String salt;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AesBytesEncryptor aesBytesEncryptor() {
		return new AesBytesEncryptor(password, salt);
	}
}
