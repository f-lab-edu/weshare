package com.flab.batch.paymentBatch.job.exception;

public class PublishPaymentException extends RuntimeException {
	public PublishPaymentException() {
	}

	public PublishPaymentException(String message) {
		super(message);
	}

	public PublishPaymentException(String message, Throwable cause) {
		super(message, cause);
	}
}
