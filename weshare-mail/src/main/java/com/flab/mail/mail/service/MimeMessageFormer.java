package com.flab.mail.mail.service;

import com.flab.mail.mail.dto.EmailDto;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

public interface MimeMessageFormer {
	MimeMessage form(EmailDto emailDto) throws MessagingException;
}
