package com.flab.weshare.utils.securityUtils;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ResourceNotFoundFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		String requestURI = request.getRequestURI();

		// 특정 조건에 따라 404 에러 반환 (예: 특정 URL에 대한 접근 제한)
		if (shouldReturn404(requestURI)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
			return; // 필터 체인 중지
		}

		// 조건에 해당하지 않는 경우, 다음 필터로 진행
		filterChain.doFilter(request, response);
	}

	// 404 에러를 반환할 조건을 정의
	private boolean shouldReturn404(String requestURI) {
		// 예: "/restricted" 경로에 대한 요청은 404 반환
		return "/restricted".equals(requestURI);
	}
}
