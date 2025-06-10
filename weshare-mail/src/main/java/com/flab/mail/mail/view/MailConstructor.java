package com.flab.mail.mail.view;

import com.flab.mail.mail.dto.AccountInfoMailDto;
import com.flab.mail.mail.dto.EmailDto;
import com.flab.mail.mail.dto.PartyJoinMailDto;
import com.flab.mail.mail.dto.SuccessPartyExtensionMailDto;

public interface MailConstructor {
	EmailDto constructRegularPaidMail(SuccessPartyExtensionMailDto successPartyExtensionMailDto);

	EmailDto constructOttAccountInfoMail(AccountInfoMailDto accountInfoMailDto);

	EmailDto constructPartyJoinMail(PartyJoinMailDto partyJoinMailDto);
}
