package com.flab.wesharepay.service;

public interface PaymentProcessor {
	String requestBillingKey(CardInfo cardEnrollRequest, Long userId);

	Receipt requestPayment(String billingKey, Integer amount, Long orderId);
}
