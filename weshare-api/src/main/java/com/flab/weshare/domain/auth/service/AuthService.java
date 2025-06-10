package com.flab.weshare.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flab.core.entity.User;
import com.flab.core.infra.UserRepository;
import com.flab.weshare.domain.auth.dto.LoginRequest;
import com.flab.weshare.domain.auth.dto.LoginResponse;
import com.flab.weshare.exception.ErrorCode;
import com.flab.weshare.exception.exceptions.CommonClientException;
import com.flab.weshare.utils.jwt.JwtAuthentication;
import com.flab.weshare.utils.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

	private final TokenManager tokenManager;
	private final JwtUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public LoginResponse login(final LoginRequest loginRequest) {
		User userByEmailAndPassword = userRepository.findByEmail(
			loginRequest.email()
		).orElseThrow(() -> new CommonClientException(ErrorCode.USER_NOT_FOUND));

		if (!passwordEncoder.matches(loginRequest.password(), userByEmailAndPassword.getPassword())) {
			throw new CommonClientException(ErrorCode.WRONG_PASSWORD);
		}

		log.info("{}번 회원 로그인", userByEmailAndPassword.getId());

		return createLoginResponse(userByEmailAndPassword.getId());
	}

	public void logout(final JwtAuthentication jwtAuthentication) {
		tokenManager.removeToken(jwtAuthentication.getId());

		log.info("{}번 회원 로그아웃", jwtAuthentication.getId());
	}

	@Transactional
	public LoginResponse reIssue(final JwtAuthentication jwtAuthentication) {
		tokenManager.validateToken(jwtAuthentication.getId(), jwtAuthentication.getToken());
		log.info("{}번 회원 액세스 토큰 재발행", jwtAuthentication.getId());
		return createLoginResponse(jwtAuthentication.getId());
	}

	private LoginResponse createLoginResponse(final Long userId) {
		String accessToken = jwtUtil.createAccessToken(userId);
		String refreshToken = jwtUtil.createRefreshToken(userId);

		tokenManager.saveToken(userId, refreshToken);

		return LoginResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}
}
