package com.flab.mail.mail.service;

import static com.flab.mail.mail.service.RedisConfiguration.*;

import java.time.LocalDateTime;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.flab.mail.mail.dto.AccountInfoMailDto;
import com.flab.mail.mail.dto.PartyJoinMailDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MailPublisher {
	private final RedisTemplate<String, Object> redisObjectTemplate;

	@Async
	public void publishAccountInfoMessage(String email, String ottId, String ottPassword, String ottName) {
		AccountInfoMailDto accountInfoMailDto = new AccountInfoMailDto(email, ottId, ottPassword, ottName);
		redisObjectTemplate.convertAndSend(OTT_ACCOUNT_INFO_QUEUE, accountInfoMailDto);
	}

	@Async
	public void publishSuccessPartyJoinMessage(String email, LocalDateTime joinDate, String nickName,
		LocalDateTime expirationDate,
		String ottAccountId, String ottPassword, String ottName) {

		PartyJoinMailDto partyJoinMailDto = new PartyJoinMailDto(email, joinDate.toLocalDate(), nickName,
			expirationDate.toLocalDate(),
			ottAccountId, ottPassword, ottName);

		log.info("Publishing party join message: {}", partyJoinMailDto);
		redisObjectTemplate.convertAndSend(SUCCESS_PARTY_JOIN_QUEUE, partyJoinMailDto);
	}
}
