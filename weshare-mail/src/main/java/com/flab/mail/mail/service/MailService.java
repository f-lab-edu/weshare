package com.flab.mail.mail.service;

import java.io.IOException;

import com.flab.mail.mail.dto.EmailDto;
import com.flab.mail.mail.dto.EmailResponseDto;

import jakarta.mail.MessagingException;

public interface MailService {
	EmailResponseDto sendMail(EmailDto emailDto) throws MessagingException, IOException;
}
