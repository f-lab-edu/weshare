package com.flab.weshare.utils.jwt;

import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtHolder {
	private final Jws<Claims> claims;
	private final String token;

	public Long getUserId() {
		return Long.parseLong(String.valueOf(claims.getBody().get("userId")));
	}

	public String getToken() {
		return token;
	}

	public boolean isAccessToken() {
		return claims.getHeader().get("token").toString().equals("access");
	}

	public boolean isRefreshToken() {
		return claims.getHeader().get("token").toString().equals("refresh");
	}

	public Date getExpirationTime() {
		return claims.getBody().getExpiration();
	}
}
