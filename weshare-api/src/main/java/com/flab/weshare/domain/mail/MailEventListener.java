package com.flab.weshare.domain.mail;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.flab.weshare.domain.mail.dto.EmailDto;
import com.flab.weshare.domain.mail.event.JoinPaidEvent;
import com.flab.weshare.domain.mail.event.RegularPaidEvent;
import com.flab.weshare.domain.mail.service.MailService;
import com.flab.weshare.domain.mail.view.MailConstructor;
import com.flab.weshare.domain.party.dto.ContractRenewalResponse;
import com.flab.weshare.domain.party.dto.SignContractResponse;
import com.flab.weshare.domain.party.service.PartyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailEventListener {
	private final MailService mailService;
	private final PartyService partyService;
	private final MailConstructor mailConstructor;

	@Async
	@TransactionalEventListener
	public void handle(final RegularPaidEvent regularPaidEvent) {
		log.info("RegularPaidEvent listen = {}", regularPaidEvent);
		try {
			ContractRenewalResponse contractRenewalResponse = partyService.formContractRenewalResponse(
				regularPaidEvent.getPartyCapsuleId());
			EmailDto regularPaidMail = mailConstructor.constructRegularPaidMail(contractRenewalResponse,
				regularPaidEvent.getPaidAmount().getIntegerAmount());
			mailService.sendMail(regularPaidMail);
		} catch (Exception e) {
			log.error("RegularPaidEvent handle 중 예외 발생 event = {}", regularPaidEvent, e);
		}
	}

	@Async
	@TransactionalEventListener
	public void handle(final JoinPaidEvent joinPaidEvent) {
		log.info("JoinPaidEvent listen = {}", joinPaidEvent);
		try {
			SignContractResponse signContractResponse = partyService.formSignContractResponse(
				joinPaidEvent.getPartyCapsuleId());
			EmailDto joinPaidMail = mailConstructor.constructJoinPaidMail(signContractResponse,
				joinPaidEvent.getPaidAmount().getIntegerAmount());
			mailService.sendMail(joinPaidMail);
		} catch (Exception e) {
			log.error("JoinPaidEvent handle 중 예외 발생 event = {}", joinPaidEvent, e);
		}
	}
}
