package com.flab.weshare.domain.auth.service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.flab.weshare.exception.ErrorCode;
import com.flab.weshare.exception.exceptions.CommonClientException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisTokenManager implements TokenManager {
	private static final String TOKEN_PREFIX = "weshare:LoginToken:";

	@Value("${jwt.refresh_expiration_time}")
	private long refreshTokenExpirationTime;
	private final RedisTemplate<String, String> redisTemplate;

	@Override
	public void saveToken(Long id, String token) {
		redisTemplate.opsForValue().set(
			getKey(id),
			token,
			refreshTokenExpirationTime,
			TimeUnit.MILLISECONDS
		);
	}

	private String getKey(Long id) {
		return TOKEN_PREFIX + id;
	}

	@Override
	public void validateToken(Long id, String token) {
		String savedRefreshToken = Optional.ofNullable(
				redisTemplate.opsForValue().getAndDelete(getKey(id)))
			.orElseThrow(() -> new CommonClientException(ErrorCode.NOT_AUTHORIZED_USER));

		if (!savedRefreshToken.equals(token)) {
			throw new CommonClientException(ErrorCode.INVALID_REFRESH_TOKEN);
		}
	}

	@Override
	public void removeToken(Long id) {
		redisTemplate.opsForValue().getAndDelete(getKey(id));
	}
}
