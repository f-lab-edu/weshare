package com.flab.weshare.domain.pay.controller;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.weshare.domain.base.BaseResponse;
import com.flab.weshare.domain.pay.service.CardService;
import com.flab.wesharepay.service.TossPaymentProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class BillingKeyController {
	private final TossPaymentProcessor tossPaymentProcessor;
	private final CardService cardService;
	private final ObjectMapper objectMapper;

	@GetMapping("/issue-billingKey")
	@ResponseStatus(HttpStatus.CREATED)
	public BaseResponse issueBillingKey(String customerKey, String authKey) throws IOException {
		log.info("customerKey:{},authKey:{}", customerKey, authKey);
		JSONObject responseObject = tossPaymentProcessor.requestBillingKey(customerKey, authKey);
		Long generatedBillingKey = cardService.enrollBillingKey(responseObject);
		return BaseResponse.success(generatedBillingKey);
	}
}
