package com.flab.weshare.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.flab.weshare.utils.jwt.JwtAuthorizationFilter;
import com.flab.weshare.utils.jwt.JwtExceptionHandlerFilter;
import com.flab.weshare.utils.jwt.RestAuthenticationEntryPoint;
import com.flab.weshare.utils.securityUtils.NotFoundFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class SecurityConfiguration {
	private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
	private final JwtAuthorizationFilter jwtAuthorizationFilter;
	private final JwtExceptionHandlerFilter jwtExceptionHandlerFilter;
	private final NotFoundFilter notFoundFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
			.formLogin(AbstractHttpConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable)
			.cors(getCorsConfigurerCustomizer())
			.httpBasic(AbstractHttpConfigurer::disable)
			.sessionManagement(
				sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests((authorizeRequests) -> authorizeRequests
				.requestMatchers(HttpMethod.GET, "/server")
				.permitAll()
				.requestMatchers("/swagger/*").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/*/user").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/*/user/rewrite").permitAll()
				.requestMatchers("/api/*/user/check-*").permitAll()
				.requestMatchers("/api/login").permitAll()
				.requestMatchers("/favicon.ico").permitAll()
				.requestMatchers("/api/access-dev").permitAll()
				.requestMatchers("/error").permitAll()
				.requestMatchers("/api/*/issue-billingKey").permitAll()
				.requestMatchers(HttpMethod.GET, "/testBilling.html").permitAll()
				.requestMatchers(HttpMethod.GET, "/fail.html").permitAll()
				.anyRequest()
				.authenticated()
			)
			.addFilterBefore(notFoundFilter, SecurityContextHolderFilter.class)
			.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(jwtExceptionHandlerFilter, JwtAuthorizationFilter.class)
			.exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
				httpSecurityExceptionHandlingConfigurer
					.authenticationEntryPoint(restAuthenticationEntryPoint))
			.build();
	}

	private Customizer<CorsConfigurer<HttpSecurity>> getCorsConfigurerCustomizer() {
		return corsCustomizer -> corsCustomizer.configurationSource(request -> {
			CorsConfiguration config = new CorsConfiguration();
			config.setAllowedOrigins(Collections.singletonList("http://localhost:8081"));
			config.setAllowedMethods(Collections.singletonList("*"));
			config.setAllowCredentials(true);
			config.setAllowedHeaders(Collections.singletonList("*"));
			return config;
		});
	}
}
