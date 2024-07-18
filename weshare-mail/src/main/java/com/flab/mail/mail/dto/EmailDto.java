package com.flab.mail.mail.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
@Builder
public class EmailDto {
	private final String toAddress;
	private final String body;
	private final String subject;
	private final boolean isHtml;
}
