package com.flab.weshare.utils.securityUtils;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MyAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {
	private final HttpRequestEndpointChecker endpointChecker;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException {
		if (!endpointChecker.isEndpointExist(request)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
		} else {
			super.commence(request, response, authException);
		}
	}
}

