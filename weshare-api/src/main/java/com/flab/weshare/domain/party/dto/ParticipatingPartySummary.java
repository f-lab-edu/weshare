package com.flab.weshare.domain.party.dto;

import java.time.LocalDate;

import com.flab.core.entity.PartyCapsule;

public record ParticipatingPartySummary(Long partyCapsuleId, LocalDate startDate, String ottName) {
	public static ParticipatingPartySummary of(PartyCapsule partyCapsule) {
		return new ParticipatingPartySummary(partyCapsule.getId(), partyCapsule.getJoinDate(),
			partyCapsule.getParty().getOtt().getName());
	}
}
