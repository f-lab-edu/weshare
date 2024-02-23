package com.flab.weshare.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flab.weshare.domain.auth.dto.LoginRequest;
import com.flab.weshare.domain.auth.dto.LoginResponse;
import com.flab.weshare.domain.auth.entity.RefreshToken;
import com.flab.weshare.domain.auth.repository.AuthRepository;
import com.flab.weshare.domain.user.entity.User;
import com.flab.weshare.domain.user.repository.UserRepository;
import com.flab.weshare.exception.ErrorCode;
import com.flab.weshare.exception.exceptions.CommonClientException;
import com.flab.weshare.exception.exceptions.UnacceptedAuthrizationException;
import com.flab.weshare.utils.jwt.JwtAuthentication;
import com.flab.weshare.utils.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;
	private final AuthRepository authRepository;
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

	@Transactional
	public void logout(JwtAuthentication jwtAuthentication) {
		RefreshToken logOutRefreshToken = RefreshToken.builder()
			.token(jwtAuthentication.getToken())
			.expiredDate(jwtAuthentication.getExpirationTime())
			.build();

		authRepository.save(logOutRefreshToken);
	}

	@Transactional
	public LoginResponse reIssue(JwtAuthentication jwtAuthentication) {
		if (authRepository.existsByToken(jwtAuthentication.getToken())) {
			throw new UnacceptedAuthrizationException(ErrorCode.ALREADY_LOGGED_OUT);
		}

		return createLoginResponse(jwtAuthentication.getId());
	}

	private LoginResponse createLoginResponse(Long userId) {
		String accessToken = jwtUtil.createAccessToken(userId);
		String refreshToken = jwtUtil.createRefreshToken(userId);

		return LoginResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}
}
