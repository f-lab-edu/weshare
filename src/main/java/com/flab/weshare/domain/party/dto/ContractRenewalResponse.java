package com.flab.weshare.domain.party.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ContractRenewalResponse {
	private final String emailAddress;
	private final LocalDate expiredDate;
	private final String ottName;
}
