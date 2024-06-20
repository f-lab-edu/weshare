package com.flab.weshare.domain.party.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.flab.core.entity.PartyCapsule;

import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ParticipantSummary(String nickName, LocalDate startDate, String status) {
	public static ParticipantSummary of(PartyCapsule partyCapsule) {
		if (partyCapsule.isEmptyCapsule()) {
			return ParticipantSummary.builder().status("empty").build();
		}
		return ParticipantSummary.builder()
			.nickName(partyCapsule.getPartyMember().getNickName())
			.startDate(partyCapsule.getJoinDate())
			.status("occupied").build();
	}
}
