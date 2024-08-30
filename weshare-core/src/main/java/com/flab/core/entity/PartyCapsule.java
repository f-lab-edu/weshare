package com.flab.core.entity;

import java.time.LocalDate;

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
public class PartyCapsule extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "party_capsule_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User partyMember;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "party_id")
	private Party party;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ott_id")
	private Ott ott;

	@Enumerated(value = EnumType.STRING)
	private PartyCapsuleStatus partyCapsuleStatus;

	private LocalDate expirationDate;

	private LocalDate joinDate;

	private boolean cancelReservation;

	@Builder
	public PartyCapsule(Long id, User partyMember, Party party, PartyCapsuleStatus partyCapsuleStatus,
		LocalDate expirationDate, LocalDate joinDate, boolean cancelReservation, Ott ott) {
		this.id = id;
		this.partyMember = partyMember;
		this.party = party;
		this.partyCapsuleStatus = partyCapsuleStatus;
		this.expirationDate = expirationDate;
		this.joinDate = joinDate;
		this.cancelReservation = cancelReservation;
		this.ott = ott;
	}

	public static PartyCapsule makeEmptyCapsule(Party party) {
		return PartyCapsule.builder()
			.party(party)
			.partyCapsuleStatus(PartyCapsuleStatus.EMPTY)
			.ott(party.getOtt())
			.build();
	}

	public boolean isEmptyCapsule() {
		return this.partyCapsuleStatus.equals(PartyCapsuleStatus.EMPTY);
	}

	public void deleteCapsule() {
		this.partyCapsuleStatus = PartyCapsuleStatus.DELETED;
	}

	public void occupy(final User user) {
		this.partyMember = user;
		this.partyCapsuleStatus = PartyCapsuleStatus.PRE_OCCUPIED;
	}

	public boolean isNeededNewPayment(LocalDate payDate) {
		return this.expirationDate.isBefore(payDate);
	}

	public void changeToOccupy() {
		this.partyCapsuleStatus = PartyCapsuleStatus.OCCUPIED;
	}

	public void changeExpirationDate(LocalDate renewExpDate) {
		this.expirationDate = renewExpDate;
	}
}
