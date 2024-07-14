package com.flab.weshare.domain.party.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignContractResponse {
	private final String emailAddress;
	private final LocalDate expiredDate;
	private final String ottName;
	private final String ottAccountId;
	private final String getOttAccountPassword;
}
