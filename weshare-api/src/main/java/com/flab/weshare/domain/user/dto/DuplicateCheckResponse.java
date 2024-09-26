package com.flab.weshare.domain.user.dto;

public record DuplicateCheckResponse(
	String target,
	boolean isDuplicated) {
}
