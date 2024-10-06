package com.flab.weshare.domain.party.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.flab.weshare.domain.party.dto.AccountInfoMailDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MailPublisher {
	private final RedisTemplate<String, Object> redisObjectTemplate;

	@Async
	public void publishAccountInfoMessage(String email, String ottId, String ottPassword, String ottName) {
		AccountInfoMailDto accountInfoMailDto = new AccountInfoMailDto(email, ottId, ottPassword, ottName);
		redisObjectTemplate.convertAndSend("mail-ott-account-info", accountInfoMailDto);
	}
}
