package com.flab.mail.mail.service;

import java.time.ZoneId;

import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import com.flab.mail.mail.dto.EmailDto;
import com.flab.mail.mail.dto.EmailResponseDto;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@EnableRetry
public class MailServiceImpl implements MailService {
	private final JavaMailSender javaMailSender;
	private final MimeMessageFormer mimeMessageFormer;

	@Retryable(retryFor = {MailSendException.class}
		, maxAttempts = 2
		, backoff = @Backoff(delay = 3000))
	@Override
	public EmailResponseDto sendMail(EmailDto emailDto) throws MessagingException {
		MimeMessage mail = mimeMessageFormer.form(emailDto);
		javaMailSender.send(mail);

		return new EmailResponseDto(
			mail.getSentDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
			null
		);
	}

	@Recover
	public EmailResponseDto logSendError(MailSendException e, EmailDto emailDto) {
		log.error("email 발송 에러 - 제목 : {} - 수신주소 : {}", emailDto.getSubject(), emailDto.getToAddress(), e);
		return new EmailResponseDto(
			null,
			e.getMessage()
		);
	}

}
