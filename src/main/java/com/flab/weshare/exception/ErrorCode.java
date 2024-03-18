package com.flab.weshare.exception;

import lombok.Getter;

@Getter
public class ErrorCode {
	public static final ErrorCode INTRENAL_SERVER_ERROR = new ErrorCode("internal_server_error", "서버에 에러가 발생했습니다.");
	public static final ErrorCode DUPLICATE_EMAIL = new ErrorCode("duplicate_email", "이미 존재하는 이메일입니다.");
	public static final ErrorCode DUPLICATE_NICKNAME = new ErrorCode("duplicate_nickname", "이미 존재하는 닉네임입니다.");
	public static final ErrorCode INVALID_INPUT = new ErrorCode("invalid_input", "입력값이 올바르지 않습니다.");
	public static final ErrorCode USER_NOT_FOUND = new ErrorCode("user_not_found", "입력한 로그인 정보에 해당하는 유저가 없습니다.");
	public static final ErrorCode INVALID_ID = new ErrorCode("INVALID_ID", "ID에 해당하는 유저가 없습니다.");
	public static final ErrorCode WRONG_PASSWORD = new ErrorCode("WRONG_PASSWORD", "비밀번호가 일치하지 않습니다.");
	public static final ErrorCode ALREADY_LOGGED_OUT = new ErrorCode("ALREADY_LOGGEND_OUT", "이미 로그아웃한 상태입니다.");
	public static final ErrorCode MALFORMED_JWT = new ErrorCode("MALFORMED_JWT", "잘못된 JWT 서명입니다.");
	public static final ErrorCode EXPIRED_JWT = new ErrorCode("EXPIRED_JWT", "만료된 JWT 토큰입니다.");
	public static final ErrorCode UNSUPPORTED_JWT = new ErrorCode("UNSUPPORTED_JWT", "지원되지 않는 JWT 토큰입니다.");
	public static final ErrorCode ILLEGAL_JWT = new ErrorCode("ILLEGAL_JWT", "JWT 토큰이 잘못되었습니다.");
	public static final ErrorCode INVALID_CAPACITY = new ErrorCode("INVALID_CAPACITY", "파티의 정원수가 잘못되었습니다.");
	public static final ErrorCode INSUFFICIENT_CAPACITY = new ErrorCode("INSUFFICIENT_CAPACITY",
		"현재 파티인원보다 작을 수 없습니다.");
	private static final ErrorCode RESOURCE_NOT_FOUND = new ErrorCode("RESOURCE_NOT_FOUND", " 리소스를 찾을 수 없습니다.");

	private final String errorCode;
	private final String errorMessage;

	public ErrorCode(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public static ErrorCode makeSpecificResourceNotFoundErrorCode(String resourceName) {
		return new ErrorCode(RESOURCE_NOT_FOUND.errorCode, resourceName + RESOURCE_NOT_FOUND.errorMessage);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ErrorCode errorCode1 = (ErrorCode)o;

		if (getErrorCode() != null ? !getErrorCode().equals(errorCode1.getErrorCode()) :
			errorCode1.getErrorCode() != null)
			return false;
		return getErrorMessage() != null ? getErrorMessage().equals(errorCode1.getErrorMessage()) :
			errorCode1.getErrorMessage() == null;
	}

	@Override
	public int hashCode() {
		int result = getErrorCode() != null ? getErrorCode().hashCode() : 0;
		result = 31 * result + (getErrorMessage() != null ? getErrorMessage().hashCode() : 0);
		return result;
	}
}
