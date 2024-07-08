package com.flab.batch.paymentBatch.job;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import com.flab.batch.paymentBatch.OttMemoryCache;
import com.flab.batch.paymentBatch.PayJobParameter;
import com.flab.batch.paymentBatch.PriceCalculatePolicy;
import com.flab.batch.paymentBatch.exception.PublishPaymentException;
import com.flab.batch.paymentBatch.job.skiplistner.PublishPaymentSkipListener;
import com.flab.core.entity.Card;
import com.flab.core.entity.Money;
import com.flab.core.entity.PartyCapsule;
import com.flab.core.entity.Payment;
import com.flab.core.infra.CardRepository;
import com.flab.core.infra.PartyCapsuleRepository;
import com.flab.core.infra.PaymentRepository;

import jakarta.persistence.EntityManagerFactory;
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
	private final OttMemoryCache ottMemoryCache;
	private final PriceCalculatePolicy priceCalculatePolicy;
	private final CardRepository cardRepository;
	private final PayJobParameter parameter;
	private final StepLogger stepLogger;
	private final EntityManagerFactory entityManagerFactory;

	private long cnt = 0l;
	private final Payment payment = Payment.builder().build();

	@Bean
	@JobScope
	public Step initialPayStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("initialPayStep", jobRepository)
			.<PartyCapsule, Payment>chunk(CHUNKSIZE, transactionManager)
			.reader(jpaPagingItemReader())
			.processor(testItemProcessor())
			.writer(itemWriter())
			.faultTolerant()
			.skip(PublishPaymentException.class)
			.skip(DataAccessException.class)
			.skipLimit(CHUNKSIZE + 50)
			.listener(publishPaymentSkipListener())
			.listener(stepLogger)
			.build();
	}

	@Bean
	@StepScope
	public ZeroOffsetRepositoryItemReader<PartyCapsule> zeroOffsetRepositoryItemReader() {
		return new ZeroOffsetRepositoryItemReaderBuilder<PartyCapsule>()
			.repository(partyCapsuleRepository)
			.methodName("findFetchTestPartyCapsuleByStatus")
			.pageSize(CHUNKSIZE)
			.arguments(parameter.getStatus())
			.setPkColumn(Long.class, "party_capsule_id")
			.name("zeroOffsetRepositoryItemReader")
			.build();
	}

	@Bean
	@StepScope
	public RepositoryItemReader<PartyCapsule> repositoryItemReader() {
		return new RepositoryItemReaderBuilder<PartyCapsule>()
			.repository(partyCapsuleRepository)
			.methodName("findFetchPartyCapsuleByStatus")
			.pageSize(CHUNKSIZE)
			.sorts(Collections.singletonMap("id", Sort.Direction.ASC))
			.arguments(parameter.getStatus())
			.name("repositoryItemReader")
			.build();
	}

	@Bean
	@StepScope
	public ZeroOffsetJpaPagingItemReader<PartyCapsule> zeroOffsetJpaPagingItemReader() {
		return new ZeroOffsetJpaPagingItemReaderBuilder<PartyCapsule>()
			.entityClass(PartyCapsule.class)
			.name("ZeroOffsetJpaPagingItemReader")
			.queryString(
				"select pc from PartyCapsule pc join fetch pc.partyMember where pc.partyCapsuleStatus=:status")
			.parameterValues(Map.of("status", parameter.getStatus()))
			.pageSize(CHUNKSIZE)
			.build();
	}

	@Bean
	@StepScope
	public JpaPagingItemReader<PartyCapsule> jpaPagingItemReader() {
		return new JpaPagingItemReaderBuilder<PartyCapsule>()
			.name("JpaPagingItemReader")
			.queryString(
				"select pc from PartyCapsule pc join fetch pc.partyMember where pc.partyCapsuleStatus=:status")
			.parameterValues(Map.of("status", parameter.getStatus()))
			.entityManagerFactory(entityManagerFactory)
			.pageSize(CHUNKSIZE)
			.build();
	}

	@Bean
	@StepScope
	public ItemProcessor<PartyCapsule, Payment> publishPaymentProcessor() {
		return partyCapsule -> {
			if (!partyCapsule.isNeededNewPayment(parameter.getPayJobDate()) || partyCapsule.isCancelReservation()) {
				return null;
			}
			Card card = cardRepository.getReferenceById(partyCapsule.getPartyMember().getAvailableCard().getId());
			Money perDayPrice = ottMemoryCache.getPerDayPriceById(partyCapsule.getOtt().getId());
			Money calculatePrice = getCalculatePrice(partyCapsule.getExpirationDate(), perDayPrice);
			return Payment.generateEmptyPayment(partyCapsule, card, calculatePrice, parameter.getPayJobDate());
		};
	}

	@Bean
	@StepScope
	public ItemProcessor<PartyCapsule, Payment> testItemProcessor() {
		return partyCapsule -> {
			return payment;
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
	public ItemWriter<Payment> itemWriter() {
		return chunk -> {
			log.info("쓰기 완료 {}개", cnt += chunk.size());
		};
	}

	@Bean
	@StepScope
	public SkipListener<PartyCapsule, Payment> publishPaymentSkipListener() {
		return new PublishPaymentSkipListener();
	}
}
