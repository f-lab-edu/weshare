package com.flab.mail.mail.view;

import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.flab.mail.mail.dto.EmailDto;
import com.flab.mail.mail.dto.SuccessPartyExtensionMailDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MailConstructorImpl implements MailConstructor {
	private final SpringTemplateEngine templateEngine;

	@Override
	public EmailDto constructRegularPaidMail(SuccessPartyExtensionMailDto successPartyExtensionMailDto) {
		return EmailDto.builder()
			.toAddress(successPartyExtensionMailDto.emailAddress())
			.isHtml(true)
			.subject("Weshare Ott 구독 결제 승인 안내")
			.body(createRegularPaidMailBody(successPartyExtensionMailDto))
			.build();
	}

	private String createRegularPaidMailBody(SuccessPartyExtensionMailDto successPartyExtensionMailDto) {
		Context context = new Context();
		context.setVariable("mailInfo", successPartyExtensionMailDto);
		return templateEngine.process("partyExtensionMail", context);
	}
}
