package com.flab.weshare.domain.pay.entity;

import com.flab.weshare.domain.base.BaseEntity;
import com.flab.weshare.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Enumerated(value = EnumType.STRING)
	private CardStatus cardStatus;

	@Builder
	public Card(Long id, String cardNumber, String billingKey, User user, CardStatus cardStatus) {
		this.id = id;
		this.cardNumber = cardNumber;
		this.billingKey = billingKey;
		this.user = user;
		this.cardStatus = cardStatus;
	}

	public static Card buildNewCard(@NonNull final User user,
		@NonNull final String billingKey,
		@NonNull final String encryptCardNumber) {
		return Card.builder()
			.billingKey(billingKey)
			.cardNumber(encryptCardNumber)
			.cardStatus(CardStatus.AVAILABLE)
			.user(user)
			.build();
	}

	public boolean isAvailable() {
		return this.getCardStatus().equals(CardStatus.AVAILABLE);
	}
}
