package com.flab.weshare.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.flab.weshare.domain.base.BaseResponse;
import com.flab.weshare.domain.user.dto.LoginRequest;
import com.flab.weshare.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {
	private final UserService userService;

	@ResponseStatus(HttpStatus.OK)
	@PostMapping("/login")
	public BaseResponse loginAttempt(@RequestBody LoginRequest loginRequest) {
		return BaseResponse.success(userService.login(loginRequest));
	}
}
