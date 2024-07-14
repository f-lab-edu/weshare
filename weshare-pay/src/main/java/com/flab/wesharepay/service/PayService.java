package com.flab.wesharepay.service;

public interface PayService {
	String enrollCard(final CardInfo cardInfo, final Long userId);

	Receipt payRequest(final String billingKey, final Integer amount, final Long orderId) throws
		InterruptedException;
}
