package com.flab.weshare.exception.exceptions;

import com.flab.weshare.exception.ErrorCode;

import lombok.Getter;

@Getter
public class DuplicateException extends CommonClientException {
	public DuplicateException(ErrorCode errorCode) {
		super(errorCode);
	}
}
