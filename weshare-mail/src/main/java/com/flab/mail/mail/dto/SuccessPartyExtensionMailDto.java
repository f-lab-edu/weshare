package com.flab.mail.mail.dto;

import java.time.LocalDate;

public record SuccessPartyExtensionMailDto(
	String ottName,
	LocalDate previousExpirationDate,
	LocalDate renewalDate,
	LocalDate paymentDate,
	Integer price,
	String emailAddress
) {
}
