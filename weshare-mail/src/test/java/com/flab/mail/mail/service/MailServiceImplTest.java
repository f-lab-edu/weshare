package com.flab.mail.mail.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.EnableRetry;

import com.flab.mail.mail.dto.EmailDto;
import com.flab.mail.mail.dto.EmailResponseDto;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@SpringBootTest
@EnableRetry
class MailServiceImplTest {
	LocalDateTime SEND_AT = LocalDateTime.of(2024, 8, 27, 0, 0);

	@Autowired
	private MailServiceImpl mailService;

	@MockBean
	private JavaMailSender javaMailSender;

	@MockBean
	private MimeMessageFormer mimeMessageFormer;

	private MimeMessage mimeMessage;

	private EmailDto emailDto;

	@BeforeEach
	void setUp() throws MessagingException {
		emailDto = new EmailDto("test@example.com", "Test Subject", "Test Body", true);
		mimeMessage = mock(MimeMessage.class);
	}

	@Test
	@DisplayName("메일 발송에 성공할 시, 정상적인 발송시각과 빈 에러메시지를 포함하는 객체를 반환한다.")
	void testSendMailSuccess() throws MessagingException {
		when(mimeMessageFormer.form(any(EmailDto.class))).thenReturn(mimeMessage);
		when(mimeMessage.getSentDate()).thenReturn(Date.from(SEND_AT.atZone(ZoneId.systemDefault()).toInstant()));

		doNothing().when(javaMailSender).send(any(MimeMessage.class));

		EmailResponseDto responseDto = mailService.sendMail(emailDto);

		assertThat(responseDto.sendAt()).isEqualTo(SEND_AT);
		assertThat(responseDto.errorMessage()).isNull();
		verify(javaMailSender, times(1)).send(any(MimeMessage.class));
	}

	@ParameterizedTest
	@ValueSource(strings = {"모종의 이유로 실패", "서버 불안정으로 실패", "에러메세지"})
	@DisplayName("메일 발송에 실패할시 두번의 재시도를 거치고, 빈 발송시각과 에러메시지를 포함하는 객체를반환한다.")
	void testSendMailRetryAndSuccess(String errorMessage) throws MessagingException {
		when(mimeMessageFormer.form(emailDto)).thenReturn(mimeMessage);
		when(mimeMessage.getSentDate()).thenReturn(Date.from(SEND_AT.atZone(ZoneId.systemDefault()).toInstant()));

		doThrow(new MailSendException(errorMessage))
			.when(javaMailSender).send(any(MimeMessage.class));

		EmailResponseDto responseDto = mailService.sendMail(emailDto);

		assertThat(responseDto.sendAt()).isNull();
		assertThat(responseDto.errorMessage()).isEqualTo(errorMessage);
		verify(javaMailSender, times(2)).send(any(MimeMessage.class));
	}

	@Test
	@DisplayName("메일 발송에 한번 실패 후 재시도를 하여 성공을 하면, 정상적으로 발송시각과, 빈 에러메시지를 반환한다.")
	void testSendMailFailAndRecover() throws MessagingException {
		when(mimeMessageFormer.form(emailDto)).thenReturn(mimeMessage);
		when(mimeMessage.getSentDate()).thenReturn(Date.from(SEND_AT.atZone(ZoneId.systemDefault()).toInstant()));

		doThrow(new MailSendException("Permanent failure"))
			.doNothing()
			.when(javaMailSender).send(any(MimeMessage.class));

		EmailResponseDto responseDto = mailService.sendMail(emailDto);

		assertThat(responseDto.sendAt()).isEqualTo(SEND_AT);
		assertThat(responseDto.errorMessage()).isNull();
		verify(javaMailSender, times(2)).send(any(MimeMessage.class));
	}
}
