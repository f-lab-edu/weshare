package com.flab.weshare.domain.pay.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.flab.weshare.domain.base.BaseResponse;
import com.flab.weshare.domain.pay.dto.CardEnrollRequest;
import com.flab.weshare.domain.pay.service.CardService;
import com.flab.weshare.utils.jwt.JwtAuthentication;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class PayController {
	private final CardService cardService;

	@PostMapping("/card")
	@ResponseStatus(HttpStatus.CREATED)
	public BaseResponse enrollCard(@RequestBody @Validated final CardEnrollRequest cardEnrollRequest
		, @AuthenticationPrincipal final JwtAuthentication jwtAuthentication) {
		Long generatedCardId = cardService.enrollCard(cardEnrollRequest, jwtAuthentication.getId());
		return BaseResponse.created(generatedCardId);
	}
}
