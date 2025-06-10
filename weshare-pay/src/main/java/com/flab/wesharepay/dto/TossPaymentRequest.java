package com.flab.wesharepay.dto;

public record TossPaymentRequest(
	String customerKey,
	Integer amount,
	String orderId,
	String orderName
) {

}
