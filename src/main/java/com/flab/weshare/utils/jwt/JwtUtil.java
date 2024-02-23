package com.flab.weshare.utils.jwt;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	private final Key key;
	private final long accessTokenExpTime;
	private final long refreshTokenExpTime;

	public JwtUtil(
		@Value("${jwt.secret}") String secretKey,
		@Value("${jwt.access_expiration_time}") long accessTokenExpirationTime,
		@Value("${jwt.refresh_expiration_time}") long refreshTokenExpirationTime
	) {
		byte[] decodeKey = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(decodeKey);
		this.accessTokenExpTime = accessTokenExpirationTime;
		this.refreshTokenExpTime = refreshTokenExpirationTime;
	}

	public String createAccessToken(Long id) {
		return createToken(id, JwtProperties.ACCESS_TOKEN_NAME, accessTokenExpTime);
	}

	public String createRefreshToken(Long id) {
		return createToken(id, JwtProperties.REFRESH_TOKEN_NAME, refreshTokenExpTime);
	}

	private String createToken(Long id, String tokenType, long expirationTime) {
		Claims claims = Jwts.claims();
		claims.put(JwtProperties.USER_ID, id.toString());

		ZonedDateTime publishedTime = ZonedDateTime.now();

		return Jwts.builder()
			.setHeaderParam(JwtProperties.TOKEN_TYPE, tokenType)
			.setClaims(claims)
			.setIssuedAt(Date.from(publishedTime.toInstant()))
			.setExpiration(Date.from(publishedTime.plusSeconds(expirationTime).toInstant()))
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	public Jws<Claims> parseToken(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token);
	}
}
