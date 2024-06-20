package com.flab.weshare.domain.party.dto;

import java.time.LocalDate;

import com.flab.core.entity.Party;

public record LeadingPartySummary(Long partyId, LocalDate startDate, String ottName) {
	public static LeadingPartySummary of(Party party) {
		return new LeadingPartySummary(party.getId(), party.getCreatedDate().toLocalDate(), party.getOtt().getName());
	}
}
