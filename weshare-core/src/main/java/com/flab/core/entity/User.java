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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USERS")
@Getter
public class User extends BaseEntity {
	@Id
	@Column(name = "user_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String email;
	private String password;
	private String nickName;
	private String telephone;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "available_card")
	private Card availableCard;

	// @BatchSize(size = 10)
	// @OneToMany(mappedBy = "user")
	// private List<Card> cards = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	private Role role;

	@Builder
	private User(String email, String password, String nickName, String telephone, Role role, Card availableCard) {
		this.email = email;
		this.password = password;
		this.nickName = nickName;
		this.telephone = telephone;
		this.role = role;
		this.availableCard = availableCard;
	}

	// public Card findAvailableCard() {
	// 	return this.cards.stream()
	// 		.filter(Card::isAvailable)
	// 		.findAny()
	// 		.orElseThrow(() -> new RuntimeException("사용가능한 카드가 없습니다."));
	// }

	public void enrollNewAvailableCard(final Card card) {
		if (card.isAvailable()) {
			this.availableCard = card;
		}
	}
}
