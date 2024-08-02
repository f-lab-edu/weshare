package com.flab.core.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class PaymentResult {
	@Enumerated(EnumType.STRING)
	private PayResultStatus payResultStatus;

	private String receipt;

	private String errorMessage;

	@Builder
	public PaymentResult(PayResultStatus payResultStatus, String receipt, String errorMessage) {
		this.payResultStatus = payResultStatus;
		this.receipt = receipt;
		this.errorMessage = errorMessage;
	}
}
