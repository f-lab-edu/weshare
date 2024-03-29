package com.flab.weshare.domain.party.entity;

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

	@Enumerated(value = EnumType.STRING)
	private PartyCapsuleStatus partyCapsuleStatus;

	@Builder
	private PartyCapsule(User partyMember, Party party, PartyCapsuleStatus partyCapsuleStatus) {
		this.partyMember = partyMember;
		this.party = party;
		this.partyCapsuleStatus = partyCapsuleStatus;
	}

	public static PartyCapsule makeEmptyCapsule(Party party) {
		return PartyCapsule.builder()
			.party(party)
			.partyCapsuleStatus(PartyCapsuleStatus.EMPTY)
			.build();
	}

	public boolean isEmptyCapsule() {
		return this.partyCapsuleStatus.equals(PartyCapsuleStatus.EMPTY);
	}

	public void deleteCapsule() {
		this.partyCapsuleStatus = PartyCapsuleStatus.DELETED;
	}
}
