package com.flab.weshare.domain.party.dto;

import java.time.LocalDate;
import java.util.List;

import com.flab.weshare.domain.party.entity.Party;
import com.flab.weshare.domain.party.entity.PartyCapsule;

public record PartyInfo(Long partyId,
						List<ParticipantSummary> participants,
						LocalDate startDate,
						String ottName,
						String ottAccountId,
						String ottAccountPassword) {

	public static PartyInfo of(final Party party) {
		return new PartyInfo(
			party.getId(),
			participantSummaries(party.getPartyCapsules()),
			party.getCreatedDate().toLocalDate(),
			party.getOtt().getName(),
			party.getOttAccountId(),
			party.getOttAccountPassword()
		);
	}

	private static List<ParticipantSummary> participantSummaries(final List<PartyCapsule> capsules) {
		return capsules.stream()
			.map(ParticipantSummary::of)
			.toList();
	}
}
