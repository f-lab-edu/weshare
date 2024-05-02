package com.flab.weshare.domain.mail.view;

import com.flab.weshare.domain.mail.dto.EmailDto;
import com.flab.weshare.domain.party.dto.ContractRenewalResponse;
import com.flab.weshare.domain.party.dto.SignContractResponse;

public interface MailConstructor {
	EmailDto constructRegularPaidMail(ContractRenewalResponse contractRenewalResponse, Integer paidAmount);

	EmailDto constructJoinPaidMail(SignContractResponse signContractResponse, Integer paidAmount);
}
