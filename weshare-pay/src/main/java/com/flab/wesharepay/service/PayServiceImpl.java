package com.flab.wesharepay.service;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayServiceImpl implements PayService {
	private final PaymentProcessor paymentProcessor;

	@Override
	public String enrollCard(final CardInfo cardInfo, final Long userId) {
		return paymentProcessor.requestBillingKey(cardInfo, userId);
	}

	@Override
	public Receipt payRequest(final String billingKey, final Integer amount, final Long orderId) {
		return paymentProcessor.requestPayment(billingKey, amount, orderId);
	}
}
