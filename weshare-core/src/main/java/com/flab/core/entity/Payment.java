package com.flab.core.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "party_capsule_id")
	private PartyCapsule partyCapsule;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "card_id")
	private Card card;

	@Embedded
	private Money amount;

	private LocalDate payDate;

	@OneToOne(mappedBy = "payment", fetch = FetchType.LAZY)
	private PayResult payResult;

	@Embedded
	private PaymentResult paymentResult;

	@Builder
	public Payment(Long id, PartyCapsule partyCapsule, Card card, Money amount, LocalDate payDate, PayResult payResult,
		PaymentResult paymentResult) {
		this.id = id;
		this.partyCapsule = partyCapsule;
		this.card = card;
		this.amount = amount;
		this.payDate = payDate;
		this.payResult = payResult;
		this.paymentResult = paymentResult;
	}

	public static Payment generateEmptyPayment(final PartyCapsule partyCapsule, final Card card, final Money amount,
		final LocalDate payDate) {
		return Payment.builder()
			.partyCapsule(partyCapsule)
			.card(card)
			.payDate(payDate)
			.amount(amount)
			.build();
	}
}
