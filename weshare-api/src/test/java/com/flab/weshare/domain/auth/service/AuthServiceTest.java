package com.flab.weshare.domain.auth.service;

import static com.flab.weshare.domain.utils.TestUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.ThrowableAssert.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.flab.core.entity.User;
import com.flab.core.infra.UserRepository;
import com.flab.weshare.domain.auth.dto.LoginRequest;
import com.flab.weshare.domain.auth.dto.LoginResponse;
import com.flab.weshare.exception.ErrorCode;
import com.flab.weshare.exception.exceptions.CommonClientException;
import com.flab.weshare.utils.jwt.JwtAuthentication;
import com.flab.weshare.utils.jwt.JwtUtil;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
	@InjectMocks
	AuthService authService;

	@Mock
	UserRepository userRepository;

	@Mock
	PasswordEncoder passwordEncoder;

	@Mock
	RedisTemplate<String, String> redisTemplate;

	@Mock
	ValueOperations<String, String> valueOperations;

	@Mock
	JwtUtil jwtUtil;

	@Mock
	JwtAuthentication jwtAuthentication;

	@Mock
	User user;

	LoginRequest loginRequest = new LoginRequest("emailMock@gmail.com", "123444@a");

	@Test
	void login_success() {
		given(userRepository.findByEmail(any(String.class))).willReturn(Optional.of(user));
		given(passwordEncoder.matches(any(CharSequence.class), any(String.class))).willReturn(true);
		given(user.getPassword()).willReturn(savedUser.getPassword());
		given(user.getId()).willReturn(USER_ID);
		given(redisTemplate.opsForValue()).willReturn(valueOperations);
		doNothing().when(valueOperations).set(any(), any(), anyLong(), any());

		LoginResponse loginResponse = authService.login(loginRequest);
		assertThat(loginResponse).isNotNull();

		then(jwtUtil).should(times(1)).createAccessToken(USER_ID);
		then(jwtUtil).should(times(1)).createRefreshToken(USER_ID);
	}

	@Test
	void login_with_user_no_exists() {
		given(userRepository.findByEmail(any(String.class))).willReturn(Optional.empty());

		testErrorSituation(() -> authService.login(loginRequest), CommonClientException.class,
			ErrorCode.USER_NOT_FOUND);
	}

	@Test
	void login_with_wrong_password() {
		given(userRepository.findByEmail(any(String.class))).willReturn(Optional.of(user));
		given(passwordEncoder.matches(any(CharSequence.class), any(String.class))).willReturn(false);
		given(user.getPassword()).willReturn(savedUser.getPassword());

		testErrorSituation(() -> authService.login(loginRequest), CommonClientException.class,
			ErrorCode.WRONG_PASSWORD);
	}

	@Test
	void reIssue_success() {
		given(jwtAuthentication.getToken()).willReturn("testToken");
		given(jwtAuthentication.getId()).willReturn(1L);
		given(redisTemplate.opsForValue()).willReturn(valueOperations);
		given(valueOperations.getAndDelete(anyString())).willReturn("testToken");
		doNothing().when(valueOperations).set(any(), any(), anyLong(), any());

		LoginResponse loginResponse = authService.reIssue(jwtAuthentication);
		assertThat(loginResponse).isNotNull();

		then(jwtUtil).should(times(1)).createAccessToken(USER_ID);
		then(jwtUtil).should(times(1)).createRefreshToken(USER_ID);
	}

	@DisplayName("Redis에 userId로 등록된 refresh token이 존재하지 않을시 예외를 발생한다.")
	@Test
	void reIssue_fail_alreadyLoggedOut() {
		given(jwtAuthentication.getId()).willReturn(1L);
		given(redisTemplate.opsForValue()).willReturn(valueOperations);
		given(valueOperations.getAndDelete(anyString())).willReturn(null);

		testErrorSituation(() -> authService.reIssue(jwtAuthentication), CommonClientException.class,
			ErrorCode.INVALID_REFRESH_TOKEN);
	}

	@DisplayName("Redis에 userId로 등록된 refresh token과 request 헤더의 refresh token이 다를시 예외를 일으킨다.")
	@Test
	void reIssue_fail_different_refresh_token() {
		given(jwtAuthentication.getToken()).willReturn("testToken");
		given(jwtAuthentication.getId()).willReturn(1L);
		given(redisTemplate.opsForValue()).willReturn(valueOperations);
		given(valueOperations.getAndDelete(anyString())).willReturn("notTestToken");

		testErrorSituation(() -> authService.reIssue(jwtAuthentication), CommonClientException.class,
			ErrorCode.INVALID_REFRESH_TOKEN);
	}

	private void testErrorSituation(ThrowingCallable shouldRaiseThrowable, Class expectedClass, ErrorCode errorCode) {
		assertThatThrownBy(shouldRaiseThrowable)
			.isInstanceOf(expectedClass)
			.extracting("errorCode")
			.hasFieldOrPropertyWithValue("errorCode", errorCode.getErrorCode())
			.hasFieldOrPropertyWithValue("errorMessage", errorCode.getErrorMessage());
	}
}
