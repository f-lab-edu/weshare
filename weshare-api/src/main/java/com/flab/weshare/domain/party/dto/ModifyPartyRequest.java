package com.flab.weshare.domain.party.dto;

import jakarta.validation.constraints.NotNull;

public record ModifyPartyRequest(@NotNull int capacity,
								 @NotNull String password) {
}
