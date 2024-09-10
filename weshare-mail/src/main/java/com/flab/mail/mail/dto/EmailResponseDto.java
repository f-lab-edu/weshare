package com.flab.mail.mail.dto;

import java.time.LocalDateTime;

public record EmailResponseDto(
	LocalDateTime sendAt,
	String errorMessage
) {

}
