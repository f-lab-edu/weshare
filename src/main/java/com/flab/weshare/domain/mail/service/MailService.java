package com.flab.weshare.domain.mail.service;

import com.flab.weshare.domain.mail.dto.EmailDto;

import jakarta.mail.MessagingException;

public interface MailService {
	void sendMail(EmailDto emailDto) throws MessagingException;
}
