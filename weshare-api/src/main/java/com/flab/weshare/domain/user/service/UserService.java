package com.flab.weshare.domain.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flab.core.infra.UserRepository;
import com.flab.weshare.domain.user.dto.DuplicateCheckResponse;
import com.flab.weshare.domain.user.dto.SignUpRequest;
import com.flab.weshare.exception.ErrorCode;
import com.flab.weshare.exception.exceptions.DuplicateException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public void signUp(SignUpRequest signUpRequest) {
		if (userRepository.existsByEmail(signUpRequest.email())) {
			throw new DuplicateException(ErrorCode.DUPLICATE_EMAIL);
		}
		if (userRepository.existsByNickName(signUpRequest.nickName())) {
			throw new DuplicateException(ErrorCode.DUPLICATE_NICKNAME);
		}
		String encodedPassword = passwordEncoder.encode(signUpRequest.password());
		userRepository.save(signUpRequest.convert(encodedPassword));
	}

	@Transactional(readOnly = true)
	public DuplicateCheckResponse emailDuplicateCheck(String email) {
		boolean existsByEmail = userRepository.existsByEmail(email);
		return new DuplicateCheckResponse(email, existsByEmail);
	}

	@Transactional(readOnly = true)
	public DuplicateCheckResponse nickNameDuplicateCheck(String nickName) {
		boolean existsByNickName = userRepository.existsByNickName(nickName);
		return new DuplicateCheckResponse(nickName, existsByNickName);
	}
}
