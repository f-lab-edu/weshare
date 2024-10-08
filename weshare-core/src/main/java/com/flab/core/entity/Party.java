package com.flab.core.entity;

import java.util.ArrayList;
import java.util.List;

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
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@DynamicUpdate
@Getter
public class Party extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "party_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", updatable = false, nullable = false)
	private User leader;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ott_id", updatable = false, nullable = false)
	private Ott ott;

	@Column(updatable = false, nullable = false)
	private String ottAccountId;

	@Column(nullable = false)
	private String ottAccountPassword;

	@Column(nullable = false)
	private int capacity;

	@Enumerated(value = EnumType.STRING)
	@Column(nullable = false)
	private PartyStatus partyStatus;

	@OneToMany(mappedBy = "party")
	private List<PartyCapsule> partyCapsules = new ArrayList<>(); //EMPTY, OCCUPIED 상태의 PartyCapsule 만 존재.

	@Builder
	private Party(User leader, Ott ott, String ottAccountId, String ottAccountPassword, int capacity) {
		this.leader = leader;
		this.ott = ott;
		this.ottAccountId = ottAccountId;
		this.ottAccountPassword = ottAccountPassword;
		this.capacity = capacity;
		this.partyStatus = PartyStatus.RUNNING;
	}

	public void changeCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getCapsulesSize() {
		return this.partyCapsules.size();
	}

	public int countOccupiedPartyCapsule() {
		return (int)partyCapsules.stream()
			.filter(pc -> pc.getPartyCapsuleStatus().equals(PartyCapsuleStatus.OCCUPIED))
			.count();
	}

	public void changePassword(String encodedPassword) {
		this.ottAccountPassword = encodedPassword;
	}

	public void deleteEmptyCapsules(final int newCapacity) {
		log.debug("current Capusule size = {}", getCapsulesSize());
		log.debug("current capacity = {}", this.capacity);
		log.debug("current newCapacity = {}", newCapacity);

		if (this.partyCapsules.size() <= newCapacity) {
			return;
		}

		for (PartyCapsule partyCapsule : this.partyCapsules) {
			if (partyCapsule.isEmptyCapsule()) {
				partyCapsule.deleteCapsule();
				this.partyCapsules.remove(partyCapsule);
			}

			log.debug("current partyCapsules Occupied = {}", countOccupiedPartyCapsule());
			log.debug("current Capusule size = {}", getCapsulesSize());

			if (this.partyCapsules.size() == newCapacity) {
				return;
			}
		}
	}
}
