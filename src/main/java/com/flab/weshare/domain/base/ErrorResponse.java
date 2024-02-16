package com.flab.weshare.domain.base;

import java.util.ArrayList;
import java.util.List;

import com.flab.weshare.exception.ErrorCode;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
	private String errorCode;
	private String errorMessage;
	private List<FieldError> errors;

	public static ErrorResponse of(ErrorCode errorCode) {
		return ErrorResponse.of(errorCode, new ArrayList<>());
	}

	public static ErrorResponse of(ErrorCode errorCode, List<FieldError> errors) {
		return ErrorResponse.builder()
			.errorCode(errorCode.getErrorCode())
			.errorMessage(errorCode.getErrorMessage())
			.errors(errors)
			.build();
	}

	@Getter
	public static class FieldError {
		private String field;
		private String value;
		private String cause;

		@Builder
		public FieldError(String field, String value, String cause) {
			this.field = field;
			this.value = value;
			this.cause = cause;
		}
	}
}
