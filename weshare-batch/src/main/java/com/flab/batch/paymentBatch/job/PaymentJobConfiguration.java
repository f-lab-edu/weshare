package com.flab.batch.paymentBatch.job;

import java.time.LocalDate;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.batch.paymentBatch.PayJobParameter;
import com.flab.batch.paymentBatch.job.steps.executePaymentStep.ExecutePaymentStepConfiguration;
import com.flab.batch.paymentBatch.job.steps.preBatchStep.OttReadStepConfiguration;
import com.flab.batch.paymentBatch.job.steps.publishPaymentStep.PublishPaymentStepConfiguration;
import com.flab.batch.paymentBatch.job.steps.updatePayResultFlow.UpdatePayResultFlowConfiguration;
import com.flab.batch.paymentBatch.util.CustomRunIdIncrementer;
import com.flab.core.entity.PartyCapsuleStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class PaymentJobConfiguration {
	private final OttReadStepConfiguration ottReadStepConfiguration;
	private final PublishPaymentStepConfiguration publishPaymentStepConfiguration;
	private final ExecutePaymentStepConfiguration executePaymentStepConfiguration;
	private final UpdatePayResultFlowConfiguration updatePayResultFlowConfiguration;

	@Bean
	@JobScope
	public PayJobParameter jobParameter(@Value("#{jobParameters[payDate]}") LocalDate payJobDate,
		@Value("#{jobParameters[renewExpirationDate]}") LocalDate renewExpirationDate,
		@Value("#{jobParameters[targetPartyCapsuleStatus]}") String status) {
		return new PayJobParameter(payJobDate, renewExpirationDate, PartyCapsuleStatus.valueOf(status));
	}

	@Bean
	@JobScope
	public PayResultCacheFileManager payResultCacheFileManager(@Value("${batch.pay.savepath}") String jsonSavePath,
		@Value("${batch.pay.cachesize}") Integer cacheSize,
		@Value("#{jobParameters[payDate]}") LocalDate payJobDate) {
		return new PayResultCacheFileManager(jsonSavePath, cacheSize, payJobDate, new ObjectMapper());
	}

	@Bean
	public Job initialPayJob(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
		return new JobBuilder("paymentJob", jobRepository)
			.incrementer(new CustomRunIdIncrementer())
			.start(ottReadStepConfiguration.step(jobRepository, platformTransactionManager))
			.next(publishPaymentStepConfiguration.initialPayStep(jobRepository, platformTransactionManager))
			.next(executePaymentStepConfiguration.payStep(jobRepository, platformTransactionManager))
			.split(new SimpleAsyncTaskExecutor())
			.add(updatePayResultFlowConfiguration.successFlow(jobRepository, platformTransactionManager))
			.end()
			.build();
	}
}
