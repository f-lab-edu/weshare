package com.flab.weshare.exception.advice;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.flab.weshare.domain.base.BaseResponse;
import com.flab.weshare.domain.base.ErrorResponse;
import com.flab.weshare.exception.ErrorCode;
import com.flab.weshare.exception.exceptions.CommonException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(CommonException.class)
	public BaseResponse commonExceptionHandler(CommonException commonException) {
		log.error("exception :", commonException);
		return BaseResponse.fail(ErrorResponse.of(commonException.getErrorCode()));
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(BindException.class)
	public BaseResponse commonExceptionHandler(BindException bindException) {
		log.error("exception :", bindException);
		return BaseResponse.fail(ErrorResponse.of(ErrorCode.INVALID_INPUT, extractFieldErrors(bindException)));
	}

	private List<ErrorResponse.FieldError> extractFieldErrors (BindException bindException){
		return bindException.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(error -> ErrorResponse.FieldError
				.builder()
				.field(error.getField())
				.value((String)error.getRejectedValue())
				.cause(error.getDefaultMessage())
				.build())
			.toList();
	}
}
