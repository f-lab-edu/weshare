package com.flab.weshare.domain.party.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Party 요청
 */
public record PartyJoinRequest(@NotNull Long ottId) {
}
