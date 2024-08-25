package com.flab.batch.paymentBatch.job.steps;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;

import com.flab.batch.paymentBatch.job.steps.updatePayResultFlow.sucessFlow.UpdateSuccessPaymentStep;
import com.flab.core.entity.PartyCapsuleStatus;
import com.flab.core.entity.Payment;

class UpdateSuccessPaymentStepTest extends BaseUnitStepTest {
	static final String STEP_NAME = UpdateSuccessPaymentStep.STEP_NAME;

	@Test
	public void successUpdateStepNoData() {
		JobExecution jobExecution = jobLauncherTestUtils.launchStep(STEP_NAME, jobParameters);
		assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
	}

	@Test
	void sucessUpdateStepTest() {
		List<Payment> payments = createPayments(batchDataGenerator.getPartyCapsules());
		JobExecution jobExecution = jobLauncherTestUtils.launchStep(STEP_NAME, jobParameters);

		jobExecution.getStepExecutions().forEach(stepExecution -> {
			assertThat(stepExecution.getReadCount()).isEqualTo(payments.size());
			assertThat(stepExecution.getWriteCount()).isEqualTo(payments.size());
		});

		partyCapsuleRepository.findAll().forEach(partyCapsule -> {
			assertThat(partyCapsule.getExpirationDate()).isEqualTo(RENEW_DATE); // 만료일자 갱신 확인
			assertThat(partyCapsule.getPartyCapsuleStatus()).isEqualTo(PartyCapsuleStatus.OCCUPIED); // 만료일자 갱신 확인
		});
		assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
	}
}
