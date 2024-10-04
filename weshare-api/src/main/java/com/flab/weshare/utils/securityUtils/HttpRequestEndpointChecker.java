package com.flab.weshare.utils.securityUtils;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HttpRequestEndpointChecker {
	private final DispatcherServlet servlet;

	public boolean isEndpointExist(HttpServletRequest request) {
		for (HandlerMapping handlerMapping : servlet.getHandlerMappings()) {
			try {
				HandlerExecutionChain foundHandler = handlerMapping.getHandler(request);
				if (foundHandler != null) {
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}
}
