package com.flab.wesharepay.exception;

import lombok.Getter;

@Getter
public class PaymentException extends RuntimeException {
	private final String errorResponse;

	public PaymentException(String errorResponse) {
		super(errorResponse);
		this.errorResponse = errorResponse;
	}
}
