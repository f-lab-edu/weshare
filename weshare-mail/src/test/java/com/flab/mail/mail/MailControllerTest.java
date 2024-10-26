package com.flab.mail.mail;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.flab.mail.mail.dto.AccountInfoMailDto;
import com.flab.mail.mail.dto.SuccessPartyExtensionMailDto;
import com.flab.mail.mail.service.MailSubService;

@ActiveProfiles("test")
@SpringBootTest
class MailControllerTest {
	@Autowired
	MailController mailController;
	@MockBean
	MailSubService mailSubService;

	@Test
	void success() {
		SuccessPartyExtensionMailDto successPartyExtensionMailDto
			= new SuccessPartyExtensionMailDto("netflix"
			, LocalDate.of(2024, 6, 1)
			, LocalDate.of(2024, 7, 2)
			, LocalDate.of(2024, 6, 1)
			, 3600
			, "jangu3384@gmail.com"
		);

		mailController.sendSuccessPartyExtensionMail(successPartyExtensionMailDto);
	}

	@Test
	void successSendAccountInfoMail() {
		AccountInfoMailDto accountInfoMailDto
			= new AccountInfoMailDto("jangu3384@gmail.com"
			, "fff222"
			, "sdafasdf22"
			, "netflix"
		);

		mailController.sendOttAccountInfo(accountInfoMailDto);
	}
}
