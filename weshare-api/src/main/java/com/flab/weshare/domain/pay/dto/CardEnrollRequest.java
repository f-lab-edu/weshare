package com.flab.weshare.domain.pay.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;

public record CardEnrollRequest(@NotNull String cardNumber,
								@NotNull @Length(min = 2, max = 2) String cardPw,
								@NotNull @Length(min = 2, max = 2) String cardExpireYear,
								@NotNull @Length(min = 2, max = 2) String cardExpireMonth,
								@NotNull @Length(min = 6, max = 6) String birthDate) {

	@Override
	public String toString() {
		return "unavailable";
	}
}
