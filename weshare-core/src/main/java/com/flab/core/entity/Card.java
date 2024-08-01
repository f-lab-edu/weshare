package com.flab.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Card extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "card_id")
	private Long id;

	private String cardNumber; //암호화

	private String billingKey; //암호화

	@Enumerated(value = EnumType.STRING)
	private CardStatus cardStatus;

	@Builder
	public Card(Long id, String cardNumber, String billingKey, CardStatus cardStatus) {
		this.id = id;
		this.cardNumber = cardNumber;
		this.billingKey = billingKey;
		this.cardStatus = cardStatus;
	}

	public static Card buildNewCard(
		@NonNull final String billingKey,
		@NonNull final String encryptCardNumber) {
		return Card.builder()
			.billingKey(billingKey)
			.cardNumber(encryptCardNumber)
			.cardStatus(CardStatus.AVAILABLE)
			.build();
	}

	public boolean isAvailable() {
		return this.getCardStatus().equals(CardStatus.AVAILABLE);
	}
}
