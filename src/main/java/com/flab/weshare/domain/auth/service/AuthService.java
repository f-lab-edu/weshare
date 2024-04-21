package com.flab.weshare.domain.auth.service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flab.weshare.domain.auth.dto.LoginRequest;
import com.flab.weshare.domain.auth.dto.LoginResponse;
import com.flab.weshare.domain.user.entity.User;
import com.flab.weshare.domain.user.repository.UserRepository;
import com.flab.weshare.exception.ErrorCode;
import com.flab.weshare.exception.exceptions.CommonClientException;
import com.flab.weshare.utils.jwt.JwtAuthentication;
import com.flab.weshare.utils.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	@Value("${jwt.refresh_expiration_time}")
	private long refreshTokenExpirationTime;

	private final RedisTemplate<String, String> redisTemplate;
	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional(readOnly = true)
	public LoginResponse login(LoginRequest loginRequest) {
		User userByEmailAndPassword = userRepository.findByEmail(
			loginRequest.email()
		).orElseThrow(() -> new CommonClientException(ErrorCode.USER_NOT_FOUND));

		if (!passwordEncoder.matches(loginRequest.password(), userByEmailAndPassword.getPassword())) {
			throw new CommonClientException(ErrorCode.WRONG_PASSWORD);
		}

		return createLoginResponse(userByEmailAndPassword.getId());
	}

	public void logout(JwtAuthentication jwtAuthentication) {
		redisTemplate.opsForValue().getAndDelete(String.valueOf(jwtAuthentication.getId()));
	}

	@Transactional
	public LoginResponse reIssue(JwtAuthentication jwtAuthentication) {
		validateRefreshToken(jwtAuthentication);
		return createLoginResponse(jwtAuthentication.getId());
	}

	private void validateRefreshToken(JwtAuthentication jwtAuthentication) {
		String savedRefreshToken = Optional.ofNullable(
				redisTemplate.opsForValue().getAndDelete(String.valueOf(jwtAuthentication.getId())))
			.orElseThrow(() -> new CommonClientException(ErrorCode.INVALID_REFRESH_TOKEN));

		if (!savedRefreshToken.equals(jwtAuthentication.getToken())) {
			throw new CommonClientException(ErrorCode.INVALID_REFRESH_TOKEN);
		}
	}

	private LoginResponse createLoginResponse(Long userId) {
		String accessToken = jwtUtil.createAccessToken(userId);
		String refreshToken = jwtUtil.createRefreshToken(userId);

		setRefreshToken(userId, refreshToken);

		return LoginResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	private void setRefreshToken(Long userId, String refreshToken) {
		redisTemplate.opsForValue().set(
			String.valueOf(userId),
			refreshToken,
			refreshTokenExpirationTime,
			TimeUnit.MILLISECONDS
		);
	}
}
