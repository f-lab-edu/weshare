package com.flab.weshare.domain.party.dto;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ParticipatedPartyDto {
	private final List<LeadingPartySummary> leadingParties;
	private final List<ParticipatingPartySummary> participatingParties;
}
