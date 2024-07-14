package com.flab.weshare.domain.party.dto;

import jakarta.validation.constraints.NotNull;

public record PartyCreationRequest(@NotNull Long ottId,
								   @NotNull String ottAccountId,
								   @NotNull String ottAccountPassword,
								   @NotNull int capacity) {

}
