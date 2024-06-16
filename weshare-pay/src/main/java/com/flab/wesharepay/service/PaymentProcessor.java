package com.flab.wesharepay.service;

import com.flab.core.entity.PayResult;
import com.flab.core.entity.Payment;

public interface PaymentProcessor {
	String requestBillingKey(CardInfo cardEnrollRequest, Long userId);

	PayResult requestPayment(String billingKey, Integer amount, Payment payment);
}
