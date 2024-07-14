package com.flab.weshare.domain.mail.view;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.flab.weshare.domain.mail.dto.EmailDto;
import com.flab.weshare.domain.party.dto.ContractRenewalResponse;
import com.flab.weshare.domain.party.dto.SignContractResponse;

@Component
public class MailConstructorImpl implements MailConstructor {
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yy년 MM월 dd일");

	@Override
	public EmailDto constructRegularPaidMail(ContractRenewalResponse contractRenewalResponse, Integer paidAmount) {
		return EmailDto.builder()
			.toAddress(contractRenewalResponse.getEmailAddress())
			.subject("WeShare ott 서비스 구독이 갱신되었습니다.")
			.body(formRegularPaidMailBody(contractRenewalResponse, paidAmount))
			.isHtml(false)
			.build();
	}

	@Override
	public EmailDto constructJoinPaidMail(SignContractResponse signContractResponse, Integer paidAmount) {
		return EmailDto.builder()
			.toAddress(signContractResponse.getEmailAddress())
			.subject("WeShare ott 서비스 가입에 성공하였습니다.")
			.body(formJoinPaidMailBody(signContractResponse, paidAmount))
			.isHtml(false)
			.build();
	}

	private String formRegularPaidMailBody(ContractRenewalResponse contractRenewalResponse, Integer paidAmount) {
		return String.format(
			"""
				사용중인 ott 서비스명 : %s
				갱신 만료일 : %s
				결제 금액 : %d원
				"""
			, contractRenewalResponse.getOttName()
			, contractRenewalResponse.getExpiredDate().format(DATE_FORMAT)
			, paidAmount
		);
	}

	private String formJoinPaidMailBody(SignContractResponse signContractResponse, Integer paidAmount) {
		return String.format(
			"""
				신청한 ott 서비스명 : %s
				갱신 만료일 : %s
				결제 금액 : %d원
								
				---------------------
				ott 계정 ID : %s
				ott 계정 PASSWORD : %s
				---------------------
				"""
			, signContractResponse.getOttName()
			, signContractResponse.getExpiredDate().format(DATE_FORMAT)
			, paidAmount
			, signContractResponse.getOttAccountId()
			, signContractResponse.getGetOttAccountPassword()
		);
	}
}
