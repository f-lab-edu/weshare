package com.flab.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
//@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class PayResult {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pay_result_id")
	private Long id;

	//@JsonIgnore
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_id")
	private Payment payment;

	@Enumerated(EnumType.STRING)
	private PayResultStatus payResultStatus;

	private String receipt;

	private String errorMessage;

	@Builder
	public PayResult(Long id, Payment payment, String receipt, PayResultStatus payResultStatus, String errorMessage) {
		this.id = id;
		this.payResultStatus = payResultStatus;
		this.payment = payment;
		this.receipt = receipt;
		this.errorMessage = errorMessage;
	}

	public static PayResult ofSuccessfulPayResult(Payment payment, String receipt) {
		return PayResult.builder()
			.payment(payment)
			.receipt(receipt)
			.payResultStatus(PayResultStatus.SUCCESS)
			.build();
	}

	public static PayResult ofRejectedPayResult(Payment payment, String receipt) {
		return PayResult.builder()
			.payment(payment)
			.receipt(receipt)
			.payResultStatus(PayResultStatus.PAY_REJECTED)
			.build();
	}

	public static PayResult ofErrorOccurPayResult(Payment payment, String errorMessage) {
		return PayResult.builder()
			.payment(payment)
			.errorMessage(errorMessage)
			.payResultStatus(PayResultStatus.ERROR_OCCUR)
			.build();
	}

	//@JsonGetter("paymentId")
	public String getPaymentId() {
		return String.valueOf(this.getPayment().getId());
	}

	@Override
	public String toString() {
		return "PayResult{" +
			"id=" + id +
			", payment=" + payment.getId() +
			", payResultStatus=" + payResultStatus +
			", receipt='" + receipt + '\'' +
			", errorMessage='" + errorMessage + '\'' +
			'}';
	}
}
