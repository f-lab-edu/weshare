package com.flab.weshare.domain.base;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
	private boolean success;
	private T data;
	private ErrorResponse errorResponse;

	public static <T> BaseResponse success(T data) {
		return BaseResponse.builder()
			.success(true)
			.data(data)
			.build();
	}

	public static BaseResponse success() {
		return BaseResponse.builder()
			.success(true)
			.build();
	}

	public static BaseResponse fail(ErrorResponse errorResponse) {
		return BaseResponse.builder()
			.success(false)
			.errorResponse(errorResponse)
			.build();
	}
}

