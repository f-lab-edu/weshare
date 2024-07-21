package com.flab.mail.mail.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.flab.mail.mail.dto.EmailDto;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SimpleMimeMessageFormer implements MimeMessageFormer {
	private final JavaMailSender javaMailSender;

	@Override
	public MimeMessage form(EmailDto emailDto) throws MessagingException {
		MimeMessage mail = javaMailSender.createMimeMessage();
		MimeMessageHelper mailHelper = new MimeMessageHelper(mail, false, "UTF-8");

		mailHelper.setTo(emailDto.getToAddress());
		mailHelper.setSubject(emailDto.getSubject());
		mailHelper.setText(emailDto.getBody(), emailDto.isHtml());

		return mail;
	}
}
