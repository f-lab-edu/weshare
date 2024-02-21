package com.flab.weshare.utils.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.flab.weshare.domain.user.entity.Role;
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

		String authorizationHeader = request.getHeader("Authorization");

		//header 예시 -> Authorization: Bearer dsfH3itgfgdfasdf...
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String token = authorizationHeader.substring(7);
			JwtHolder jwtHolder = parseToken(token);
			 //jwt 유효성검사 실패시 예외 발생.
			if (uriMatchesAccessToken(request,jwtHolder) || uriMatchesRefreshToken(request,jwtHolder)) {
				setAuthenticationFromJwt(jwtHolder);
			}
		}
		filterChain.doFilter(request, response);
	}

	private JwtHolder parseToken(String token) {
		try {
			return new JwtHolder(jwtUtil.parseToken(token), token);
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

	private void logInvalidTokenException(Exception e, String token){
		log.info("exception : {} "
			+ "token : {}"
			, e.getClass().getSimpleName(), token);
	}

	private boolean uriMatchesRefreshToken(HttpServletRequest request, JwtHolder jwtHolder) {
		return isRequireRefreshToken(request) && jwtHolder.isRefreshToken();
	}

	private boolean uriMatchesAccessToken(HttpServletRequest request, JwtHolder jwtHolder) {
		return !isRequireRefreshToken(request) && jwtHolder.isAccessToken();
	}

	
	private boolean isRequireRefreshToken(HttpServletRequest request) {
		return request.getRequestURI().endsWith("/logout") || request.getRequestURI().endsWith("/reissue");
	}

	private void setAuthenticationFromJwt(JwtHolder jwtHolder) {
		JwtAuthentication jwtAuthentication = new JwtAuthentication(jwtHolder.getToken(), jwtHolder.getUserId(), jwtHolder.getExpirationTime());
		JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwtAuthentication, List.of(
			new SimpleGrantedAuthority(Role.CLIENT.toString())));

		SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);
	}
}
