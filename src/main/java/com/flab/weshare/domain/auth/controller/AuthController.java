package com.flab.weshare.domain.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.flab.weshare.domain.auth.dto.LoginRequest;
import com.flab.weshare.domain.auth.service.AuthService;
import com.flab.weshare.domain.base.BaseResponse;
import com.flab.weshare.utils.jwt.JwtAuthentication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@ResponseStatus(HttpStatus.OK)
	@PostMapping("/login")
	public BaseResponse loginAttempt(@RequestBody LoginRequest loginRequest) {
		return BaseResponse.success(authService.login(loginRequest));
	}

	@ResponseStatus(HttpStatus.OK)
	@PostMapping("/logout")
	public BaseResponse logoutRequest(@AuthenticationPrincipal JwtAuthentication jwtAuthentication) {
		authService.logout(jwtAuthentication);
		return BaseResponse.success();
	}

	@ResponseStatus(HttpStatus.OK)
	@PostMapping("/reissue")
	public BaseResponse reIssueRequest(@AuthenticationPrincipal JwtAuthentication jwtAuthentication) {
		return BaseResponse.success(authService.reIssue(jwtAuthentication));
	}
}
