package com.flab.mail.mail.view;

import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.flab.mail.mail.dto.AccountInfoMailDto;
import com.flab.mail.mail.dto.EmailDto;
import com.flab.mail.mail.dto.PartyJoinMailDto;
import com.flab.mail.mail.dto.SuccessPartyExtensionMailDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MailConstructorImpl implements MailConstructor {
	private static final String DTO_NAME = "mailInfo";

	private final SpringTemplateEngine templateEngine;

	@Override
	public EmailDto constructRegularPaidMail(SuccessPartyExtensionMailDto successPartyExtensionMailDto) {
		return EmailDto.builder()
			.toAddress(successPartyExtensionMailDto.emailAddress())
			.isHtml(true)
			.subject("Weshare Ott 구독 결제 승인 안내")
			.body(createBodyByTemplateEngine(successPartyExtensionMailDto, "partyExtensionMail"))
			.build();
	}

	@Override
	public EmailDto constructOttAccountInfoMail(AccountInfoMailDto accountInfoMailDto) {
		return EmailDto.builder()
			.toAddress(accountInfoMailDto.email())
			.isHtml(true)
			.subject("Weshare Ott 계정 정보 발송")
			.body(createBodyByTemplateEngine(accountInfoMailDto, "ottAccountInfoMail"))
			.build();
	}

	@Override
	public EmailDto constructPartyJoinMail(PartyJoinMailDto partyJoinMailDto) {
		return EmailDto.builder()
			.toAddress(partyJoinMailDto.email())
			.isHtml(true)
			.subject("Wesahre Party 가입 안내 발송")
			.body(createBodyByTemplateEngine(partyJoinMailDto, "partyJoinMail"))
			.build();
	}

	private String createBodyByTemplateEngine(Object value, String templateName) {
		Context context = new Context();
		context.setVariable(DTO_NAME, value);
		return templateEngine.process(templateName, context);
	}
}
