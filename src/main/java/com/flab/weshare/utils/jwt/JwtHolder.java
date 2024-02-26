package com.flab.weshare.utils.jwt;

import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtHolder {
	private final String errorLog = "[jwt claim] 필드 null : ";
	private final Jws<Claims> claims;
	private final String token;

	public Long getUserId() {
		try {
			String userId = claims.getBody().get(JwtProperties.USER_ID).toString();
			return Long.parseLong(userId);
		} catch (NullPointerException ex) {
			log.info(errorLog + JwtProperties.USER_ID);
			throw new IllegalArgumentException();
		}
	}

	public String getToken() {
		return token;
	}

	public boolean isAccessToken() {
		return isRightToken(JwtProperties.ACCESS_TOKEN_NAME);
	}

	public boolean isRefreshToken() {
		return isRightToken(JwtProperties.REFRESH_TOKEN_NAME);
	}

	private boolean isRightToken(String tokenType) {
		try {
			return claims.getHeader().get(JwtProperties.TOKEN_TYPE).toString().equals(tokenType);
		} catch (NullPointerException ex) {
			log.info(errorLog + tokenType);
			throw new IllegalArgumentException();
		}
	}

	public Date getExpirationTime() {
		return claims.getBody().getExpiration();
	}
}
