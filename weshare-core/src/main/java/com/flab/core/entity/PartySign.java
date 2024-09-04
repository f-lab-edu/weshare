package com.flab.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PartySign extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "party_sign_id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "party_capsule_id")
	private PartyCapsule partyCapsule;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pay_res_id")
	private PayRes payRes;

	@Builder
	public PartySign(Long id, PartyCapsule partyCapsule, PayRes payRes) {
		this.id = id;
		this.partyCapsule = partyCapsule;
		this.payRes = payRes;
	}
}
