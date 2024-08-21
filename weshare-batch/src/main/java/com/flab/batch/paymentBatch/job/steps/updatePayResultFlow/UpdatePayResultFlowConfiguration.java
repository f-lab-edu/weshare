package com.flab.batch.paymentBatch.job.steps.updatePayResultFlow;

import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.flab.batch.paymentBatch.job.steps.updatePayResultFlow.sucessFlow.SendMailPartyExtensionStepConfiguration;
import com.flab.batch.paymentBatch.job.steps.updatePayResultFlow.sucessFlow.UpdateSuccessPaymentStep;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class UpdatePayResultFlowConfiguration {
	private final UpdateSuccessPaymentStep updateSuccessPaymentStep;
	private final SendMailPartyExtensionStepConfiguration sendMailPartyExtensionStepConfiguration;

	@Bean
	public Flow successFlow(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new FlowBuilder<SimpleFlow>("flow1")
			.start(updateSuccessPaymentStep.updateSuccessPayment(jobRepository, transactionManager))
			.next(sendMailPartyExtensionStepConfiguration.sendMailPartyExtensionStep(jobRepository,
				transactionManager))
			.build();
	}
}
