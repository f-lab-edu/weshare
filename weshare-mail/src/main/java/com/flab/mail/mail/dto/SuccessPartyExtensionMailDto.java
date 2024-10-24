package com.flab.mail.mail.dto;

import java.io.Serializable;
import java.time.LocalDate;

public record SuccessPartyExtensionMailDto(
	String ottName,
	LocalDate previousExpirationDate,
	LocalDate renewalDate,
	LocalDate paymentDate,
	Integer price,
	String emailAddress
) implements Serializable {
	private static final long serialVersionUID = 1L;
}
