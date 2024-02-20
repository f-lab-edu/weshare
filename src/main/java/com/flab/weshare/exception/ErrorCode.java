package com.flab.weshare.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
	INTRENAL_SERVER_ERROR("internal_server_error", "서버에 에러가 발생했습니다."),
	DUPLICATE_EMAIL("duplicate_email", "이미 존재하는 이메일입니다."),
	DUPLICATE_NICKNAME("duplicate_nickname", "이미 존재하는 닉네임입니다."),
	INVALID_INPUT("invalid_input", "입력값이 올바르지 않습니다."),
	USER_NOT_FOUND("user_not_found", "입력한 로그인 정보에 해당하는 유저가 없습니다.");

	private final String errorCode;
	private final String errorMessage;

	ErrorCode(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
}
