package com.flab.mail.mail;

import java.time.LocalDate;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flab.mail.mail.dto.AccountInfoMailDto;
import com.flab.mail.mail.dto.EmailDto;
import com.flab.mail.mail.dto.EmailResponseDto;
import com.flab.mail.mail.dto.SuccessPartyExtensionMailDto;
import com.flab.mail.mail.service.MailService;
import com.flab.mail.mail.view.MailConstructor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MailController {
	private final MailService mailService;
	private final MailConstructor mailConstructor;
	private final RedisTemplate<String, Object> redisTemplate;
	private final RedisMessageListenerContainer redisMessageListenerContainer;

	private static final String CHANNEL_PARTY_EXTENSION = "mail-party-extension";

	@GetMapping("/test")
	public void sendSuccessPartyExtensionMail() {
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

		log.info("isListening() = {}", redisMessageListenerContainer.isListening());
		log.info("isActive() = {}", redisMessageListenerContainer.isActive());
		log.info("isRunning() = {}", redisMessageListenerContainer.isRunning());
	}

	public EmailResponseDto sendSuccessPartyExtensionMail(SuccessPartyExtensionMailDto successPartyExtensionMailDto) {
		try {
			EmailDto emailDto = mailConstructor.constructRegularPaidMail(successPartyExtensionMailDto);
			return mailService.sendMail(emailDto);
		} catch (Exception e) {
			log.error("이메일 에러발생", e);
			return new EmailResponseDto(null, e.getMessage());
		}
	}

	/**
	 * userEmail
	 * ottAccountId
	 * ottPassword
	 * ottname
	 */
	public EmailResponseDto sendOttAccountInfo(AccountInfoMailDto accountInfoMailDto) {
		try {
			EmailDto emailDto = mailConstructor.constructOttAccountInfoMail(accountInfoMailDto);
			return mailService.sendMail(emailDto);
		} catch (Exception e) {
			log.error("이메일 에러발생", e);
			return new EmailResponseDto(null, e.getMessage());
		}
	}
}
