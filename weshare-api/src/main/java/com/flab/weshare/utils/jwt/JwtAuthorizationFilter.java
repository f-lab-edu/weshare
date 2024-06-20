package com.flab.weshare.utils.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.flab.core.entity.Role;
import com.flab.weshare.exception.ErrorCode;
import com.flab.weshare.exception.exceptions.InvalidTokenException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String authorizationHeader = request.getHeader(JwtProperties.HEADER);

		//header 예시 -> Authorization: Bearer dsfH3itgfgdfasdf...
		if (authorizationHeader != null && authorizationHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
			String token = authorizationHeader.substring(JwtProperties.TOKEN_PREFIX_LENGTH);
			proceedAuthentication(request, token);
		}
		filterChain.doFilter(request, response);
	}

	private void proceedAuthentication(HttpServletRequest request, String token) {
		try {
			JwtHolder jwtHolder = new JwtHolder(jwtUtil.parseToken(token), token);
			if (isRefreshTokenBasedRequest(request, jwtHolder) || isAccessedTokenBasedRequest(request, jwtHolder)) {
				setAuthenticationFromJwt(jwtHolder);
			}
		} catch (MalformedJwtException | SignatureException e) {
			logInvalidTokenException(e, token);
			throw new InvalidTokenException(ErrorCode.MALFORMED_JWT);
		} catch (ExpiredJwtException e) {
			logInvalidTokenException(e, token);
			throw new InvalidTokenException(ErrorCode.EXPIRED_JWT);
		} catch (UnsupportedJwtException e) {
			logInvalidTokenException(e, token);
			throw new InvalidTokenException(ErrorCode.UNSUPPORTED_JWT);
		} catch (IllegalArgumentException e) {
			logInvalidTokenException(e, token);
			throw new InvalidTokenException(ErrorCode.ILLEGAL_JWT);
		}
	}

	private void logInvalidTokenException(Exception e, String token) {
		log.info("exception : {} "
				+ "token : {}"
			, e.getClass().getSimpleName(), token);
	}

	private boolean isRefreshTokenBasedRequest(HttpServletRequest request, JwtHolder jwtHolder) {
		return isRequireRefreshToken(request) && jwtHolder.isRefreshToken();
	}

	private boolean isAccessedTokenBasedRequest(HttpServletRequest request, JwtHolder jwtHolder) {
		return !isRequireRefreshToken(request) && jwtHolder.isAccessToken();
	}

	private boolean isRequireRefreshToken(HttpServletRequest request) {
		return request.getRequestURI().endsWith("/logout") || request.getRequestURI().endsWith("/reissue");
	}

	private void setAuthenticationFromJwt(JwtHolder jwtHolder) {
		JwtAuthentication jwtAuthentication = new JwtAuthentication(jwtHolder);
		JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwtAuthentication, List.of(
			new SimpleGrantedAuthority(Role.CLIENT.toString())));

		SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);
	}
}
