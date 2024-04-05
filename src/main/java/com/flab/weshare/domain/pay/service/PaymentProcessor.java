package com.flab.weshare.domain.pay.service;

import com.flab.weshare.domain.pay.dto.CardEnrollRequest;
import com.flab.weshare.domain.pay.entity.Payment;
import com.flab.weshare.domain.paymentBatch.PayResult;

public interface PaymentProcessor {
	String requestBillingKey(CardEnrollRequest cardEnrollRequest, Long userId);

	PayResult requestPayment(String billingKey, Integer amount, Payment payment);
}
