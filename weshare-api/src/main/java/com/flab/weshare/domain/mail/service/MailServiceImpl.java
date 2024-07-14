package com.flab.weshare.domain.mail.service;

import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.flab.weshare.domain.mail.dto.EmailDto;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableRetry
public class MailServiceImpl implements MailService {
	private final JavaMailSender javaMailSender;

	@Retryable(retryFor = {MailSendException.class}
		, maxAttempts = 2
		, backoff = @Backoff(delay = 3000))
	@Override
	public void sendMail(EmailDto emailDto) throws MessagingException {
		MimeMessage mail = javaMailSender.createMimeMessage();
		MimeMessageHelper mailHelper = new MimeMessageHelper(mail, false, "UTF-8");

		mailHelper.setTo(emailDto.getToAddress());
		mailHelper.setSubject(emailDto.getSubject());
		mailHelper.setText(emailDto.getBody(), emailDto.isHtml());

		javaMailSender.send(mail);
	}

	@Recover
	public void logSendError(MailSendException e, EmailDto emailDto) {
		log.error("email 발송 오류 내용 = {}", emailDto, e);
	}
}
