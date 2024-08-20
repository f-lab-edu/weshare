package com.flab.batch.paymentBatch.job.steps.updatePayResultFlow.sucessFlow;

import com.flab.core.entity.PartyExtension;
import com.flab.mail.mail.dto.EmailResponseDto;

public class PartyExtensionMailPacker {
	public PartyExtension partyExtension;
	public EmailResponseDto emailResponseDto;

	public PartyExtensionMailPacker(PartyExtension partyExtension, EmailResponseDto emailResponseDto) {
		this.partyExtension = partyExtension;
		this.emailResponseDto = emailResponseDto;
	}
}
