package com.flab.weshare.domain.pay.service;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.core.entity.BillingKey;
import com.flab.core.infra.BillingKeyRepository;
import com.flab.core.infra.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardService {
	private final UserRepository userRepository;
	private final BillingKeyRepository billingKeyRepository;
	private final ObjectMapper objectMapper;

	@Transactional
	public Long enrollBillingKey(final JSONObject billingKeyResponse) {
		try {
			BillingKey billingKey = objectMapper.readValue(billingKeyResponse.toJSONString(), BillingKey.class);
			log.info("billingKey : {}", billingKey);
			billingKey.setUser(
				userRepository.findByClientId(billingKeyResponse.get("customerKey").toString())
					.orElseThrow(() -> new RuntimeException("User not found")
					));
			billingKeyRepository.save(billingKey);
			return billingKey.getId();
		} catch (JsonProcessingException e) {
			//Todo 로그 남기고 Exception handler에서 처리하도록 변경
			log.error("billingKeyResponse json parsing error : {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
