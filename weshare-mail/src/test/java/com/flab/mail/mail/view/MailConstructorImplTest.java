package com.flab.mail.mail.view;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import com.flab.mail.mail.dto.EmailDto;
import com.flab.mail.mail.dto.PartyJoinMailDto;
import com.flab.mail.mail.dto.SuccessPartyExtensionMailDto;

class MailConstructorImplTest {
	MailConstructorImpl mailConstructor;

	@BeforeEach
	void setUp() {
		// 실제 SpringTemplateEngine 인스턴스를 생성하고, 이를 MailConstructorImpl에 주입
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver());
		mailConstructor = new MailConstructorImpl(templateEngine);
	}

	private ITemplateResolver templateResolver() {
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		// src/main/resources/templates 디렉토리를 가리키도록 설정
		templateResolver.setPrefix("templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode("HTML");
		templateResolver.setCharacterEncoding("UTF-8");
		return templateResolver;
	}

	@Test
	void sucess() {
		SuccessPartyExtensionMailDto successPartyExtensionMailDto
			= new SuccessPartyExtensionMailDto("netflix"
			, LocalDate.of(2024, 6, 1)
			, LocalDate.of(2024, 7, 2)
			, LocalDate.of(2024, 6, 30)
			, 3600
			, "test@test.com"
		);

		EmailDto emailDto = mailConstructor.constructRegularPaidMail(successPartyExtensionMailDto);
		System.out.println(emailDto);
	}

	@Test
	void sucessPartyjoin() {
		PartyJoinMailDto partyJoinMailDto = new PartyJoinMailDto(
			"jangu3394@gmail.com",
			LocalDate.of(2025, 5, 25),
			"test22",
			LocalDate.of(2025, 6, 25),
			"asdfdf",
			"adsf22",
			"넷플릭스"
		);
		EmailDto emailDto = mailConstructor.constructPartyJoinMail(partyJoinMailDto);
		System.out.println(emailDto);
	}
}
