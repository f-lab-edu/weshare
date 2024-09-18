package com.flab.weshare.utils.securityUtils;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.weshare.domain.base.BaseResponse;
import com.flab.weshare.domain.base.ErrorResponse;
import com.flab.weshare.exception.ErrorCode;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotFoundFilter extends OncePerRequestFilter {
	private final ObjectMapper objectMapper;
	private final RequestMappingHandlerMapping handlerMapping;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException, IOException {
		try {
			HandlerExecutionChain handler = handlerMapping.getHandler(request);

			if (handler == null && !request.getRequestURI().contains("swagger") && !request.getRequestURI()
				.endsWith(".html")) {
				log.info("요청한 URL에 해당하는 핸들러가 없습니다. {}", request.getRequestURI());
				response.reset();
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				response.setContentType("application/json; charset=UTF-8");
				BaseResponse resp = BaseResponse.fail(ErrorResponse.of(ErrorCode.NOT_FOUND));
				response.getWriter()
					.write(objectMapper.writeValueAsString(resp));
				return;
			}
		} catch (Exception e) {
			// Handler 탐색 실패도 404로 간주
			response.reset();
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.setContentType("application/json; charset=UTF-8");
			BaseResponse resp = BaseResponse.fail(ErrorResponse.of(ErrorCode.NOT_FOUND));
			response.getWriter()
				.write(objectMapper.writeValueAsString(resp));
			return;
		}

		// 존재하는 핸들러 → 다음 필터로
		filterChain.doFilter(request, response);
	}
}
