package com.flab.mail.mail.dto;

import java.io.Serializable;

public record AccountInfoMailDto(
	String email,
	String ottAccountId,
	String ottPassword,
	String ottName) implements Serializable {
	private static final long serialVersionUID = 1L;
}
