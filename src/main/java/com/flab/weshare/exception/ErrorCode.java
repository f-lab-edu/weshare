package com.flab.weshare.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
	INTRENAL_SERVER_ERROR("internal_server_error", "서버에 에러가 발생했습니다."),
	DUPLICATE_EMAIL("duplicate_email", "이미 존재하는 이메일입니다."),
	DUPLICATE_NICKNAME("duplicate_nickname", "이미 존재하는 닉네임입니다."),
	INVALID_INPUT("invalid_input", "입력값이 올바르지 않습니다."),
	USER_NOT_FOUND("user_not_found", "입력한 로그인 정보에 해당하는 유저가 없습니다."),
	INVALID_ID("INVALID_ID", "ID에 해당하는 유저가 없습니다."),
	WRONG_PASSWORD("WRONG_PASSWORD", "비밀번호가 일치하지 않습니다."),
	ALREADY_LOGGED_OUT("ALREADY_LOGGEND_OUT", "이미 로그아웃한 상태입니다."),
	MALFORMED_JWT("MALFORMED_JWT", "잘못된 JWT 서명입니다."),
	EXPIRED_JWT("EXPIRED_JWT", "만료된 JWT 토큰입니다."),
	UNSUPPORTED_JWT("UNSUPPORTED_JWT", "지원되지 않는 JWT 토큰입니다."),
	ILLEGAL_JWT("ILLEGAL_JWT", "JWT 토큰이 잘못되었습니다.");

	private final String errorCode;
	private final String errorMessage;

	ErrorCode(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
}
