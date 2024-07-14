package com.flab.weshare.utils.jwt;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.weshare.domain.base.BaseResponse;
import com.flab.weshare.domain.base.ErrorResponse;
import com.flab.weshare.exception.exceptions.InvalidTokenException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtExceptionHandlerFilter extends OncePerRequestFilter {
	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (InvalidTokenException e) {
			setErrorResponse(e, response);
		}
	}

	private void setErrorResponse(InvalidTokenException e, HttpServletResponse response) throws
		IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json; charset=UTF-8");

		BaseResponse fail = BaseResponse.fail(ErrorResponse.of(e.getErrorCode()));

		response.getWriter().write(objectMapper.writeValueAsString(fail));
	}
}
