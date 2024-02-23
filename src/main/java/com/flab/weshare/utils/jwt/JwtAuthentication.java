package com.flab.weshare.utils.jwt;

import java.util.Date;

import lombok.Getter;

@Getter
public class JwtAuthentication {
	private final String token;
	private final Long id;
	private final Date expirationTime;

	public JwtAuthentication(JwtHolder jwtHolder) {
		this.token = jwtHolder.getToken();
		this.id = jwtHolder.getUserId();
		this.expirationTime = jwtHolder.getExpirationTime();
	}
}
