package com.flab.weshare.domain.partyMatch.service.dto;

import static com.flab.weshare.domain.partyMatch.service.PartyJoinListener.*;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	include = JsonTypeInfo.As.PROPERTY,
	property = "status"
)
@JsonSubTypes({
	@JsonSubTypes.Type(value = Queued.class, name = QUEUED),
	@JsonSubTypes.Type(value = Found.class, name = FOUND)
})
public abstract class PartyJoinLuaResult {
	String status;

	public PartyJoinLuaResult(String status) {
		this.status = status;
	}
}
