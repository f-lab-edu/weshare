package com.flab.batch.paymentBatch.job.steps;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.flab.batch.paymentBatch.job.steps.preBatchStep.OttMemoryCache;
import com.flab.core.entity.EmailSentStatus;
import com.flab.core.entity.PartyCapsule;
import com.flab.core.entity.PartyExtension;
import com.flab.core.entity.Payment;
import com.flab.mail.mail.MailController;
import com.flab.mail.mail.dto.EmailResponseDto;

class SendMailPartyExtensionUnitStepTestTest extends BaseUnitStepTest {
	@MockBean
	private MailController mailController;
	@MockBean
	private OttMemoryCache ottMemoryCache;

	private final EmailResponseDto SUCCESS_RESPONSE
		= new EmailResponseDto(LocalDateTime.of(2024, 8, 27, 0, 0), null);

	private final EmailResponseDto FAIL_RESPONSE
		= new EmailResponseDto(null, "메일 발송에 실패했습니다.");

	@Test
	public void sendMailUpdateStepNoData() {
		JobExecution jobExecution = jobLauncherTestUtils.launchStep("sendMailPartyExtensionStep", jobParameters);
		assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
	}

	/**
	 * -검증하고자 하는것.
	 * 1. partyExtensionReader에서 partyExtension을 읽어오는것.
	 * 2. asyncPartyExtensionMailProcessor에서 partyExtension을 받아서 mailEventListener.sendSuccessPartyExtensionMail을 호출하는것.
	 * 3. writer에서 메일 발송 결과를 업데이트 하는것.
	 */
	@Test
	public void sendMailUpdateStepWithSuccess() {
		List<PartyCapsule> partyCapsules = batchDataGenerator.getPartyCapsules();

		List<Payment> payments = createPayments(partyCapsules);
		List<PartyExtension> partyExtensions = createPartyExtensions(payments);

		when(mailController.sendSuccessPartyExtensionMail(any())).thenReturn(SUCCESS_RESPONSE);
		when(ottMemoryCache.getOttById(anyLong())).thenReturn(batchDataGenerator.getOtt());

		JobExecution jobExecution = jobLauncherTestUtils.launchStep("sendMailPartyExtensionStep", jobParameters);
		assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

		jobExecution.getStepExecutions().forEach(stepExecution -> {
			assertThat(stepExecution.getReadCount()).isEqualTo(partyExtensions.size());
			assertThat(stepExecution.getWriteCount()).isEqualTo(partyExtensions.size());
			assertThat(stepExecution.getCommitCount()).isEqualTo(partyExtensions.size());
		});

		List<PartyExtension> results = partyExtensionRepository.findAllById(
			partyExtensions.stream().map(PartyExtension::getId).toList());

		assertThat(results).allMatch(partyExtension -> partyExtension.getEmailSendResult().getEmailSentStatus()
			.equals(EmailSentStatus.SENT_SUCCESS));
	}
}
