package com.flab.weshare.domain.party.dto;

import java.time.LocalDate;

import com.flab.weshare.domain.party.entity.PartyCapsule;

import lombok.Builder;

@Builder
public record PartyCapsuleInfo(Long partyCapsuleId,
							   LocalDate startDate,
							   LocalDate expirationDate,
							   String ottName,
							   boolean cancelReservation,
							   String status) {
	public static PartyCapsuleInfo of(PartyCapsule partyCapsule) {
		return PartyCapsuleInfo.builder()
			.partyCapsuleId(partyCapsule.getId())
			.expirationDate(partyCapsule.getExpirationDate())
			.cancelReservation(partyCapsule.isCancelReservation())
			.ottName(partyCapsule.getParty().getOtt().getName())
			.startDate(partyCapsule.getJoinDate())
			.status(partyCapsule.getPartyCapsuleStatus().toString())
			.build();
	}
}
