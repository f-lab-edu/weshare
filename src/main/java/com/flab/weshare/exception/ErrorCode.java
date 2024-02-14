package com.flab.weshare.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
	DUPLICATE_EMAIL("duplicate_email","이미 존재하는 이메일입니다."),
	DUPLICATE_NICKNAME("duplicate_nickname","이미 존재하는 닉네임입니다."),
	INVALID_INPUT("invalid_input","입력값이 올바르지 않습니다.");

	private final String errorCode;
	private final String errorMessage;

	ErrorCode(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
}
