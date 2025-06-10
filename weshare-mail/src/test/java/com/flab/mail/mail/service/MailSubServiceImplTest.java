package com.flab.mail.mail.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.flab.mail.mail.dto.SuccessPartyExtensionMailDto;

@ActiveProfiles("test")
@SpringBootTest
class MailSubServiceImplTest {
	@Autowired
	RedisTemplate<?, ?> redisTemplate;

	private static final String CHANNEL_PARTY_EXTENSION = "mail-party-extension";

	//@Test
	void sendSuccessPartyExtensionMail() throws InterruptedException {
		SuccessPartyExtensionMailDto successPartyExtensionMailDto =
			new SuccessPartyExtensionMailDto(
				"netflix",
				LocalDate.now(),
				LocalDate.now(),
				LocalDate.now(),
				3000,
				"jangu3384@gmail.com"
			);

		redisTemplate.convertAndSend(CHANNEL_PARTY_EXTENSION, successPartyExtensionMailDto);

		Thread.sleep(100000);
	}

}
