package com.flab.weshare.domain.user.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;

import com.flab.weshare.domain.base.BaseEntity;
import com.flab.weshare.domain.pay.entity.Card;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

	@BatchSize(size = 10)
	@OneToMany(mappedBy = "user")
	private List<Card> cards = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	private Role role;

	@Builder
	private User(String email, String password, String nickName, String telephone, Role role) {
		this.email = email;
		this.password = password;
		this.nickName = nickName;
		this.telephone = telephone;
		this.role = role;
	}

	public Card findAvailableCard() {
		return this.cards.stream()
			.filter(Card::isAvailable)
			.findAny()
			.orElseThrow(() -> new RuntimeException("사용가능한 카드가 없습니다."));
	}
}
