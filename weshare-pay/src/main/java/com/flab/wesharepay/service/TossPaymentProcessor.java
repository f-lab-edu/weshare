package com.flab.wesharepay.service;

import java.io.IOException;

import org.json.simple.JSONObject;

import com.flab.wesharepay.dto.TossPaymentRequest;

public interface TossPaymentProcessor {
	JSONObject requestBillingKey(final String customerKey, final String authKey) throws IOException;

	JSONObject requestPayment(String billingKey, TossPaymentRequest tossPaymentRequest) throws IOException;
}
