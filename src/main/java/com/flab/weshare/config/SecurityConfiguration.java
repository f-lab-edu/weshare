package com.flab.weshare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.flab.weshare.utils.jwt.JwtAuthorizationFilter;
import com.flab.weshare.utils.jwt.JwtExceptionHandlerFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfiguration {
	private final JwtAuthorizationFilter jwtAuthorizationFilter;
	private final JwtExceptionHandlerFilter jwtExceptionHandlerFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
			.formLogin(AbstractHttpConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.sessionManagement(
				sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests((authorizeRequests) -> authorizeRequests
				.requestMatchers(HttpMethod.POST, "/api/*/user").permitAll()
				.requestMatchers("/api/login").permitAll()
				.anyRequest().authenticated()
			)
			.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(jwtExceptionHandlerFilter, JwtAuthorizationFilter.class)
			.build();
	}
}
