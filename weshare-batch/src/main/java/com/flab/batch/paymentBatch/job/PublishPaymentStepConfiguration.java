package com.flab.batch.paymentBatch.job;

import java.time.LocalDate;
import java.util.Collections;

import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import com.flab.batch.paymentBatch.PayJobParameter;
import com.flab.batch.paymentBatch.PriceCalculatePolicy;
import com.flab.batch.paymentBatch.exception.PublishPaymentException;
import com.flab.batch.paymentBatch.job.skiplistner.PublishPaymentSkipListener;
import com.flab.core.entity.Card;
import com.flab.core.entity.Money;
import com.flab.core.entity.PartyCapsule;
import com.flab.core.entity.Payment;
import com.flab.core.infra.PartyCapsuleRepository;
import com.flab.core.infra.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class PublishPaymentStepConfiguration {
	@Value("${batch.pay.chunksize}")
	private int CHUNKSIZE;
	private final PartyCapsuleRepository partyCapsuleRepository;
	private final PaymentRepository paymentRepository;
	private final PriceCalculatePolicy priceCalculatePolicy;
	private final PayJobParameter parameter;

	@Bean
	@JobScope
	public Step initialPayStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("initialPayStep", jobRepository)
			.<PartyCapsule, Payment>chunk(CHUNKSIZE, transactionManager)
			.reader(occupiedPartyCapsuleItemReader())
			.processor(publishPaymentProcessor())
			.writer(paymentRepositoryItemWriter())
			.faultTolerant()
			.skip(PublishPaymentException.class)
			.skip(DataAccessException.class)
			.skipLimit(CHUNKSIZE + 50)
			.listener(publishPaymentSkipListener())
			.build();
	}

	@Bean
	@StepScope
	public RepositoryItemReader<PartyCapsule> occupiedPartyCapsuleItemReader() {
		return new RepositoryItemReaderBuilder<PartyCapsule>()
			.repository(partyCapsuleRepository)
			.methodName("findFetchPartyCapsuleByStatus")
			.pageSize(CHUNKSIZE)
			.arguments(parameter.getStatus())
			.sorts(Collections.singletonMap("createdDate", Sort.Direction.ASC))
			.name("occupiedPartyCapsuleItemReader")
			.build();
	}

	@Bean
	@StepScope
	public ItemProcessor<PartyCapsule, Payment> publishPaymentProcessor() {
		return partyCapsule -> {
			if (!partyCapsule.isNeededNewPayment(parameter.getPayJobDate()) || partyCapsule.isCancelReservation()) {
				return null;
			}
			Card card = partyCapsule.getPartyMember().findAvailableCard();
			Money perDayPrice = partyCapsule.getParty().getOtt().getPerDayPrice();
			Money calculatePrice = getCalculatePrice(partyCapsule.getExpirationDate(), perDayPrice);
			return Payment.generateEmptyPayment(partyCapsule, card, calculatePrice, parameter.getPayJobDate());
		};
	}

	private Money getCalculatePrice(LocalDate expDate, Money perDayPrice) {
		return priceCalculatePolicy.calculatePrice(expDate, parameter.getRenewExpirationDate(), perDayPrice);
	}

	@Bean
	@StepScope
	public RepositoryItemWriter<Payment> paymentRepositoryItemWriter() {
		return new RepositoryItemWriterBuilder<Payment>()
			.repository(paymentRepository)
			.methodName("save")
			.build();
	}

	@Bean
	@StepScope
	public SkipListener<PartyCapsule, Payment> publishPaymentSkipListener() {
		return new PublishPaymentSkipListener();
	}
}
