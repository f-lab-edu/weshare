package com.flab.weshare.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flab.weshare.domain.user.dto.SignUpRequest;
import com.flab.weshare.domain.user.repository.UserRepository;
import com.flab.weshare.exception.ErrorCode;
import com.flab.weshare.exception.exceptions.DuplicateException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	@Transactional
	public void signUp(SignUpRequest signUpRequest) {
		if (userRepository.existsByEmail(signUpRequest.email())) {
			throw new DuplicateException(ErrorCode.DUPLICATE_EMAIL);
		}
		if (userRepository.existsByNickName(signUpRequest.nickName())) {
			throw new DuplicateException(ErrorCode.DUPLICATE_NICKNAME);
		}
		userRepository.save(signUpRequest.convert());
	}
}
