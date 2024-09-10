package com.flab.wesharepay.service;

public interface PayService {
	Receipt payRequest(final String billingKey, final Integer amount, final Long orderId) throws
		InterruptedException;
}
