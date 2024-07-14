package com.flab.weshare.exception.exceptions;

import com.flab.weshare.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UnsatisfiedAuthorityException extends RuntimeException {
	private final ErrorCode errorCode;
}
