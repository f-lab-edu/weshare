package com.flab.weshare.domain.paymentBatch.job;

import java.time.LocalDate;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.flab.weshare.domain.party.entity.PartyCapsuleStatus;
import com.flab.weshare.domain.paymentBatch.PayJobParameter;

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
	@JobScope
	public PayJobParameter jobParameter(@Value("#{jobParameters[payDate]}") LocalDate payJobDate,
		@Value("#{jobParameters[renewExpirationDate]}") LocalDate renewExpirationDate,
		@Value("#{jobParameters[targetPartyCapsuleStatus]}") PartyCapsuleStatus status) {
		return new PayJobParameter(payJobDate, renewExpirationDate, status);
	}

	@Bean
	public Job initialPayJob(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
		return new JobBuilder("paymentJob", jobRepository)
			.start(publishPaymentStepConfiguration.initialPayStep(jobRepository, platformTransactionManager))
			.next(executePaymentStepConfiguration.payStep(jobRepository, platformTransactionManager))
			.next(updatePayResultStepConfiguration.updatePayResultStep(jobRepository, platformTransactionManager))
			.build();
	}
}
