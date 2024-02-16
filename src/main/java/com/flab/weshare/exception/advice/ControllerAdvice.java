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
import com.flab.weshare.exception.exceptions.DuplicateException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(RuntimeException.class)
	public BaseResponse commonExceptionHandler(RuntimeException runtimeException) {
		log.error("exception :", runtimeException);
		return BaseResponse.fail(ErrorResponse.of(ErrorCode.INTRENAL_SERVER_ERROR));
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(DuplicateException.class)
	public BaseResponse commonExceptionHandler(DuplicateException duplicateException) {
		log.error("exception :", duplicateException);
		return BaseResponse.fail(ErrorResponse.of(duplicateException.getErrorCode()));
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
