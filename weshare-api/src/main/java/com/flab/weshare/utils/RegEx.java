package com.flab.weshare.utils;

import lombok.Getter;

@Getter
public class RegEx {
	public static class Pattern {
		public static final String NICKNAME_PATTERN = "^[가-힣A-Za-z0-9]{2,6}$";
		public static final String TELEPHONE_PATTERN = "^01(?:0|1|[6-9])(\\d{3}|\\d{4})\\d{4}$";
		public static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$";

	}

	public static class Message {
		public static final String NICKNAME_MESSAGE = "닉네임 형식에 맞지 않습니다.";
		public static final String TELEPHONE_MESSAGE = "전화번호 형식에 맞지 않습니다.";
		public static final String PASSWORD_MESSAGE = "전화번호 형식에 맞지 않습니다.";
	}
}
