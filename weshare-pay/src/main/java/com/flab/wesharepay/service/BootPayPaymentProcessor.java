package com.flab.wesharepay.service;

import org.springframework.beans.factory.annotation.Value;

public class BootPayPaymentProcessor implements PaymentProcessor {
	private static final String ORDER_NAME = "weshare ott서비스";
	private static final String PG = "나이스페이먼츠";

	private final String restApplicationId;
	private final String privateKey;

	public BootPayPaymentProcessor(@Value("${pay.rest_application_id}") String restApplicationId
		, @Value("${pay.private_key}") String privateKey) {
		this.restApplicationId = restApplicationId;
		this.privateKey = privateKey;
	}

	@Override
	public Receipt requestPayment(String billingKey, Integer amount, Long orderId) {
		return null;
	}
}
