package com.flab.wesharepay.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class PayServiceImpl implements PayService {
	private final PaymentProcessor paymentProcessor;

	@Override
	public Receipt payRequest(final String billingKey, final Integer amount, final Long orderId) {
		return paymentProcessor.requestPayment(billingKey, amount, orderId);
	}
}
