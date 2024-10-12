package com.flab.weshare.domain.user.controller;

import static com.flab.weshare.utils.RegEx.Message.*;
import static com.flab.weshare.utils.RegEx.Pattern.*;

import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.flab.weshare.domain.base.BaseResponse;
import com.flab.weshare.domain.base.ErrorResponse;
import com.flab.weshare.domain.user.dto.DuplicateCheckResponse;
import com.flab.weshare.domain.user.dto.SignUpRequest;
import com.flab.weshare.domain.user.service.UserService;
import com.flab.weshare.exception.ErrorCode;
import com.flab.weshare.exception.exceptions.CommonClientException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
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

	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/check-nickname")
	public BaseResponse checkNickName(@RequestParam @NotNull String nickname) {
		validateParameter(NICKNAME_PATTERN, nickname, NICKNAME_MESSAGE);
		DuplicateCheckResponse duplicateCheckResponse = userService.nickNameDuplicateCheck(nickname);
		return BaseResponse.success(duplicateCheckResponse);
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/check-email")
	public BaseResponse checkEmail(@RequestParam @Valid @NotNull @Email String email) {
		validateParameter(EMAIL_PATTERN, email, EMAIL_MESSAGE);
		DuplicateCheckResponse duplicateCheckResponse = userService.emailDuplicateCheck(email);
		return BaseResponse.success(duplicateCheckResponse);
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@GetMapping("/error")
	public BaseResponse checkNotFoundError() {
		return BaseResponse.fail(ErrorResponse.of(ErrorCode.WRONG_PASSWORD));
	}

	private void validateParameter(String regEx, String target, String message) {
		if (!Pattern.matches(regEx, target)) {
			throw new CommonClientException(ErrorCode.makeValidationErrorMessage(message));
		}
	}
}
