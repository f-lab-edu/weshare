package com.flab.weshare.domain.paymentBatch.job;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.flab.weshare.domain.pay.entity.Payment;
import com.flab.weshare.domain.pay.entity.PaymentStatus;
import com.flab.weshare.domain.paymentBatch.PayResultStatus;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class UpdatePayResultStepConfiguration {
	@Value("${batch.pay.chunksize}")
	private int CHUNKSIZE;
	private final EntityManagerFactory entityManagerFactory;

	@Bean
	@JobScope
	public Step updatePayResultStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("updatePayResultStep", jobRepository)
			.<Payment, Payment>chunk(CHUNKSIZE, transactionManager)
			.reader(updateItemReader())
			.writer(updateItemWriter())
			.faultTolerant()
			.processorNonTransactional()
			.build();
	}

	@Bean
	@StepScope
	public JpaPagingItemReader<Payment> updateItemReader() {
		final JpaPagingItemReader<Payment> reader = new JpaPagingItemReader<>() {
			@Override
			public int getPage() {
				return 0;
			}
		};

		reader.setQueryString(
			"select distinct pm from Payment pm "
				+ "join fetch pm.partyCapsule "
				+ "join fetch pm.payResult "
				+ "where pm.paymentStatus=:status"
		);
		reader.setParameterValues(Map.of("status", PaymentStatus.WAITING));
		reader.setEntityManagerFactory(entityManagerFactory);
		reader.setName("updateItemReader");
		reader.setPageSize(CHUNKSIZE);
		return reader;
	}

	@Bean
	@StepScope
	public ItemWriter<Payment> updateItemWriter() {
		return payments -> {
			payments.forEach(payment -> {
				if (payment.getPayResult().getPayResultStatus().equals(PayResultStatus.SUCCESS)) {
					payment.updatePayResultStatus(PaymentStatus.SUCCESS);
				} else if (payment.getPayResult().getPayResultStatus().equals(PayResultStatus.PAY_REJECTED)) {
					payment.updatePayResultStatus(PaymentStatus.FAILED);
				} else if (payment.getPayResult().getPayResultStatus().equals(PayResultStatus.ERROR_OCCUR)) {
					payment.updatePayResultStatus(PaymentStatus.TECHNICAL_ERROR);
				}

				payment.getPartyCapsule().changeExpirationDate(LocalDateTime.now().plusMonths(1));
			});
		};
	}
}
