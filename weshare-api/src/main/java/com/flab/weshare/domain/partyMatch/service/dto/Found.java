package com.flab.weshare.domain.partyMatch.service.dto;

import lombok.Getter;

@Getter
public class Found extends PartyJoinLuaResult {
	String partyJoinId;
	String userId;

	public Found(String status, String partyJoinId, String userId) {
		super(status);
		this.partyJoinId = partyJoinId;
		this.userId = userId;
	}
}
