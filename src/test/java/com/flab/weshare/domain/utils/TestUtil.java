package com.flab.weshare.domain.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.flab.weshare.domain.user.entity.User;

public class TestUtil {
	private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

	//User
	public static final Long USER_ID = 1L;
	public static final String EMAIL = "test@gmail.com";
	public static final String PASSWORD = "test1234!";
	public static final String NICKNAME = "테스트";
	public static final String TELEPHONE = "01012341234";
	public static final User savedUser = User
		.builder()
		.email(EMAIL)
		.password(bCryptPasswordEncoder.encode(PASSWORD))
		.nickName(NICKNAME)
		.telephone(TELEPHONE)
		.build();
}
