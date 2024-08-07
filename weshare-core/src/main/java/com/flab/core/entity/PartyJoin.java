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
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PartyJoin extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "party_join_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ott_id")
	private Ott ott;

	@Enumerated(value = EnumType.STRING)
	private PartyJoinStatus partyJoinStatus;

	@Builder
	private PartyJoin(User user, Ott ott, PartyJoinStatus partyJoinStatus) {
		this.user = user;
		this.ott = ott;
		this.partyJoinStatus = partyJoinStatus;
	}

	public static PartyJoin generateWaitingPartyJoin(User user, Ott ott) {
		return PartyJoin.builder()
			.partyJoinStatus(PartyJoinStatus.WAITING)
			.user(user)
			.ott(ott)
			.build();
	}

	public boolean isWaitingPartyJoin() {
		return this.partyJoinStatus.equals(PartyJoinStatus.WAITING);
	}

	public void changeStatusPayWaiting() {
		this.partyJoinStatus = PartyJoinStatus.PAY_WAITING;
	}
}
