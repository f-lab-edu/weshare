package com.flab.weshare.domain.user.dto;

public record LoginRequest(String email, String password) {
	@Override
	public String toString() {
		return "LoginRequest 요청";
	}
}

