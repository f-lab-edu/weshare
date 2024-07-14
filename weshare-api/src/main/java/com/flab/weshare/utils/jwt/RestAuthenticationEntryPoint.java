package com.flab.weshare.utils.jwt;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.weshare.domain.base.BaseResponse;
import com.flab.weshare.domain.base.ErrorResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
	private final ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException, ServletException {

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json; charset=UTF-8");

		log.info("auth exception : " + Arrays.toString(authException.getStackTrace()));

		BaseResponse fail = BaseResponse.fail(ErrorResponse.of("인증에 실패 했습니다."));

		response.getWriter().write(objectMapper.writeValueAsString(fail));
	}
}
