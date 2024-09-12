package com.flab.wesharepay.dto;

import lombok.Getter;

@Getter
public class TossIssueBillingkeyRequest {
	private final String customerKey;
	private final String authKey;

	public TossIssueBillingkeyRequest(final String customerKey, final String authKey) {
		validate(customerKey, authKey);
		this.customerKey = customerKey;
		this.authKey = authKey;
	}

	private void validate(final String billingKey, final String authKey) {
		if (customerKey == null || customerKey.isEmpty()) {
			throw new IllegalArgumentException("Customer key must not be null or empty");
		}
		if (authKey == null || authKey.isEmpty()) {
			throw new IllegalArgumentException("Auth key must not be null or empty");
		}
	}
}
