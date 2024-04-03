package com.flab.weshare.domain.pay.service;

import com.flab.weshare.domain.pay.dto.CardEnrollRequest;

public interface PaymentProcessor {
	String requestBillingKey(CardEnrollRequest cardEnrollRequest, Long userId);

	String requestPayment();
}
