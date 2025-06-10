package com.flab.weshare.domain.partyMatch.dto;

public record JoinResultDto(String type, Long id) {
	public static JoinResultDto ofAccepted(Long id) {
		return new JoinResultDto("partyCapsule", id);
	}

	public static JoinResultDto ofQueued(Long id) {
		return new JoinResultDto("partyJoin", id);
	}
}
