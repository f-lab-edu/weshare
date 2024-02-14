package com.flab.weshare.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.flab.weshare.domain.user.dto.SignUpRequest;
import com.flab.weshare.domain.user.entity.User;
import com.flab.weshare.domain.user.repository.UserRepository;
import com.flab.weshare.exception.ErrorCode;
import com.flab.weshare.exception.exceptions.CommonException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	@InjectMocks
	UserService userService;

	@Mock
	UserRepository userRepository;

	SignUpRequest signUpRequest = new SignUpRequest("test@email.com", "test", "01011111111");

	@Test
	void signUp_중복_이메일() {
		//given
		given(userRepository.existsByEmail(anyString()))
			.willReturn(true);

		assertThatThrownBy(() -> userService.signUp(signUpRequest))
			.isInstanceOf(CommonException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.DUPLICATE_EMAIL);
	}

	@Test
	void signUp_중복_닉네임() {
		//given
		given(userRepository.existsByNickName(anyString()))
			.willReturn(true);

		assertThatThrownBy(() -> userService.signUp(signUpRequest))
			.isInstanceOf(CommonException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.DUPLICATE_NICKNAME);
	}

	@Test
	void signUp_성공() {
		given(userRepository.existsByNickName(anyString())).willReturn(false);
		given(userRepository.existsByNickName(anyString())).willReturn(false);

		userService.signUp(signUpRequest);

		then(userRepository).should(times(1)).save(any(User.class));
	}
}
