package com.flab.weshare.domain.partyMatch.service.dto;

import lombok.Getter;

@Getter
public class Queued extends PartyJoinLuaResult {
	String partySlotId;

	public Queued(String status, String partySlotId) {
		super(status);
		this.partySlotId = partySlotId;
	}
}
