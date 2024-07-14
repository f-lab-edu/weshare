package com.flab.weshare.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
	private String accessToken;
	private String refreshToken;
}
