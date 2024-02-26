package com.flab.weshare.exception.exceptions;

import com.flab.weshare.exception.ErrorCode;

public class InvalidTokenException extends CommonClientException{
	public InvalidTokenException(ErrorCode errorCode) {
		super(errorCode);
	}
}
