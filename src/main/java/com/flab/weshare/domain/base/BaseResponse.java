package com.flab.weshare.domain.base;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
	private boolean success;
	private T data;
	private ErrorResponse errorResponse;

	public static <T> BaseResponse sucesss(T data) {
		return new BaseResponse(true, data, null);
	}

	public static BaseResponse success() {
		return new BaseResponse(true, null, null);
	}

	public static BaseResponse fail(ErrorResponse errorResponse) {
		return new BaseResponse(false, null, errorResponse);
	}
}

