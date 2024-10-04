package com.flab.weshare.utils.securityUtils;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MyAccessDeniedHandler extends AccessDeniedHandlerImpl {
	private HttpRequestEndpointChecker endpointChecker;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws ServletException, IOException {
		if (!endpointChecker.isEndpointExist(request)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
		} else {
			super.handle(request, response, accessDeniedException);
		}
	}
}
