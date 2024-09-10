package com.flab.mail.mail;

import org.springframework.stereotype.Component;

import com.flab.mail.mail.dto.EmailDto;
import com.flab.mail.mail.dto.EmailResponseDto;
import com.flab.mail.mail.dto.SuccessPartyExtensionMailDto;
import com.flab.mail.mail.service.MailService;
import com.flab.mail.mail.view.MailConstructor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailController {
	private final MailService mailService;
	private final MailConstructor mailConstructor;

	public EmailResponseDto sendSuccessPartyExtensionMail(SuccessPartyExtensionMailDto successPartyExtensionMailDto) {
		try {
			EmailDto emailDto = mailConstructor.constructRegularPaidMail(successPartyExtensionMailDto);
			return mailService.sendMail(emailDto);
		} catch (Exception e) {
			log.error("이메일 에러발생", e);
			return new EmailResponseDto(null, e.getMessage());
		}
	}
}
