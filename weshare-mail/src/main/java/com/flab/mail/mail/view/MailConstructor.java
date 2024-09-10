package com.flab.mail.mail.view;

import com.flab.mail.mail.dto.EmailDto;
import com.flab.mail.mail.dto.SuccessPartyExtensionMailDto;

public interface MailConstructor {
	EmailDto constructRegularPaidMail(SuccessPartyExtensionMailDto successPartyExtensionMailDto);

	//EmailDto constructJoinPaidMail();
}
