package com.flab.core.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
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
public class PartyExtension extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "party_extension_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "party_capsule_id", updatable = false, nullable = false)
	private PartyCapsule partyCapsule;

	private LocalDate previousExpirationDate;

	private LocalDate renewExpirationDate;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_id", updatable = false, nullable = false)
	private Payment payment;

	private EmailSendResult emailSendResult;

	@Builder
	public PartyExtension(Long id, PartyCapsule partyCapsule, LocalDate previousExpirationDate,
		LocalDate renewExpirationDate, Payment payment, EmailSendResult emailSendResult) {
		this.id = id;
		this.partyCapsule = partyCapsule;
		this.previousExpirationDate = previousExpirationDate;
		this.renewExpirationDate = renewExpirationDate;
		this.payment = payment;
		this.emailSendResult = emailSendResult;
	}
}
