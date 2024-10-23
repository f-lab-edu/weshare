package com.flab.mail.mail.dto;

import java.io.Serializable;
import java.time.LocalDate;

public record PartyJoinMailDto(
	String email,
	LocalDate joinDate,
	String nickName,
	LocalDate expirationDate,
	String ottAccountId,
	String ottPassword,
	String ottName) implements Serializable {
	private static final long serialVersionUID = 1L;
}
