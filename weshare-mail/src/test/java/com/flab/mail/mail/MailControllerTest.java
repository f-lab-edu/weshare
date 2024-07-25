package com.flab.mail.mail;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.flab.mail.mail.dto.SuccessPartyExtensionMailDto;

@ActiveProfiles("test")
@SpringBootTest
class MailControllerTest {
	@Autowired
	MailController mailEventListener;

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

		mailEventListener.sendSuccessPartyExtensionMail(successPartyExtensionMailDto);
	}
}
