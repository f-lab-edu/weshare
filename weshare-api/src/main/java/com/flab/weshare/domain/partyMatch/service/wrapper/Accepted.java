package com.flab.weshare.domain.partyMatch.service.wrapper;

import com.flab.core.entity.PartyCapsule;

public record Accepted(PartyCapsule partyCapsule) implements JoinResult {
}
