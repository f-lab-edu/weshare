package com.flab.weshare.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.flab.weshare.domain.base.BaseResponse;
import com.flab.weshare.domain.user.dto.SignUpRequest;
import com.flab.weshare.domain.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
	public BaseResponse signUpUser(@RequestBody @Valid SignUpRequest signUpRequest) {
		userService.signUp(signUpRequest);

		return BaseResponse.success();
	}
}
