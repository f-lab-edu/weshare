package com.flab.wesharepay.service;

public interface PaymentProcessor {
	Receipt requestPayment(String billingKey, Integer amount, Long orderId);
}
