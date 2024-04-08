package com.flab.weshare.domain.pay.entity;

import com.flab.weshare.domain.base.BaseEntity;
import com.flab.weshare.domain.base.Money;
import com.flab.weshare.domain.party.entity.PartyCapsule;
import com.flab.weshare.domain.paymentBatch.PayResult;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;

	@Embedded
	private Money amount;

	@OneToOne(mappedBy = "payment")
	private PayResult payResult;

	@Builder
	private Payment(Long id, PartyCapsule partyCapsule, Card card, PaymentStatus paymentStatus, Money amount) {
		this.id = id;
		this.partyCapsule = partyCapsule;
		this.card = card;
		this.paymentStatus = paymentStatus;
		this.amount = amount;
	}

	public static Payment generateEmptyPayment(final PartyCapsule partyCapsule, final Card card, final Money amount) {
		return Payment.builder()
			.partyCapsule(partyCapsule)
			.card(card)
			.amount(amount)
			.paymentStatus(PaymentStatus.WAITING)
			.build();
	}

	public void updatePayResultStatus(final PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
}
