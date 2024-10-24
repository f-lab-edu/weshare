package com.flab.mail.mail.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.mail.mail.dto.AccountInfoMailDto;
import com.flab.mail.mail.dto.EmailDto;
import com.flab.mail.mail.dto.SuccessPartyExtensionMailDto;
import com.flab.mail.mail.view.MailConstructor;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailSubServiceImpl implements MailSubService {
	private final ObjectMapper objectMapper;
	private final MailConstructor mailConstructor;
	private final MailService mailService;

	@Override
	public void handleSuccessPartyExtension(String message) {
		try {

			SuccessPartyExtensionMailDto successPartyExtensionMailDto = objectMapper.readValue(message,
				SuccessPartyExtensionMailDto.class);

			EmailDto emailDto = mailConstructor.constructRegularPaidMail(successPartyExtensionMailDto);
			mailService.sendMail(emailDto);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	@Override
	public void handleOttAccountInfo(String message) {
		try {
			AccountInfoMailDto accountInfoMailDto = objectMapper.readValue(message,
				AccountInfoMailDto.class);
			EmailDto emailDto = mailConstructor.constructOttAccountInfoMail(accountInfoMailDto);
			mailService.sendMail(emailDto);
		} catch (MessagingException | IOException e) {
			throw new RuntimeException(e);
		}
	}
}
