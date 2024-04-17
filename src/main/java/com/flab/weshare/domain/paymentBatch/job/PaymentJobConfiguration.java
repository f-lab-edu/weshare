package com.flab.weshare.domain.paymentBatch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class PaymentJobConfiguration {
	private final PublishPaymentStepConfiguration publishPaymentStepConfiguration;
	private final ExecutePaymentStepConfiguration executePaymentStepConfiguration;
	private final UpdatePayResultStepConfiguration updatePayResultStepConfiguration;

	@Bean
	public Job initialPayJob(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
		return new JobBuilder("paymentJob", jobRepository)
			.start(publishPaymentStepConfiguration.initialPayStep(jobRepository, platformTransactionManager))
			.next(executePaymentStepConfiguration.payStep(jobRepository, platformTransactionManager))
			.next(updatePayResultStepConfiguration.updatePayResultStep(jobRepository, platformTransactionManager))
			.build();
	}
}
