package com.flab.weshare.exception.exceptions;

import com.flab.weshare.exception.ErrorCode;

public class UnacceptedAuthrizationException extends CommonClientException{
	public UnacceptedAuthrizationException(ErrorCode errorCode) {
		super(errorCode);
	}
}
