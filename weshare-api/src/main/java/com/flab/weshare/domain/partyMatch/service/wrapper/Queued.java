package com.flab.weshare.domain.partyMatch.service.wrapper;

import com.flab.core.entity.PartyJoin;

public record Queued(PartyJoin partyJoin) implements JoinResult {
}
