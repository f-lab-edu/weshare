package com.flab.weshare.domain.base;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
	private static final String COMMON_CREATED_ID = "id";

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

	public static <T> BaseResponse created(T generatedPartyId) {
		return BaseResponse.builder()
			.success(true)
			.data(Map.of(COMMON_CREATED_ID, generatedPartyId))
			.build();
	}
}

