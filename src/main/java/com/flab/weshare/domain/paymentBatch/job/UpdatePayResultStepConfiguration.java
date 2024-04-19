package com.flab.weshare.domain.paymentBatch.job;

import java.time.LocalDate;
import java.util.Collections;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import com.flab.weshare.domain.pay.entity.Payment;
import com.flab.weshare.domain.pay.entity.PaymentStatus;
import com.flab.weshare.domain.pay.repository.PaymentRepository;
import com.flab.weshare.domain.paymentBatch.PayJobParameter;
import com.flab.weshare.domain.paymentBatch.PayResultStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class UpdatePayResultStepConfiguration {
	@Value("${batch.pay.chunksize}")
	private int CHUNKSIZE;
	private final PayJobParameter parameter;
	private final PaymentRepository paymentRepository;

	@Bean
	public Step updatePayResultStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("updatePayResultStep", jobRepository)
			.<Payment, Payment>chunk(CHUNKSIZE, transactionManager)
			.reader(targetPaymentReader())
			.processor(filterProcessor())
			.writer(updateItemWriter())
			.faultTolerant()
			.build();
	}

	@Bean
	@StepScope
	public RepositoryItemReader<Payment> targetPaymentReader() {
		return new RepositoryItemReaderBuilder<Payment>()
			.repository(paymentRepository)
			.methodName("findFetchPagePaymentByPayDate")
			.pageSize(CHUNKSIZE)
			.arguments(LocalDate.now())
			.sorts(Collections.singletonMap("createdDate", Sort.Direction.ASC))
			.name("targetPaymentReader")
			.build();
	}

	@Bean
	@StepScope
	public ItemProcessor<Payment, Payment> filterProcessor() {
		return payment -> {
			if (!payment.getPaymentStatus().equals(PaymentStatus.WAITING)) {
				return null;
			}
			return payment;
		};
	}

	@Bean
	@StepScope
	public ItemWriter<Payment> updateItemWriter() {
		return payments -> {
			payments.forEach(payment -> {
				if (payment.getPayResult().getPayResultStatus().equals(PayResultStatus.SUCCESS)) {
					payment.updatePayResultStatus(PaymentStatus.SUCCESS);
					payment.getPartyCapsule().changeExpirationDate(parameter.getRenewExpirationDate());
				} else if (payment.getPayResult().getPayResultStatus().equals(PayResultStatus.PAY_REJECTED)) {
					payment.updatePayResultStatus(PaymentStatus.FAILED);
				} else if (payment.getPayResult().getPayResultStatus().equals(PayResultStatus.ERROR_OCCUR)) {
					payment.updatePayResultStatus(PaymentStatus.TECHNICAL_ERROR);
				}
			});
		};
	}
}
