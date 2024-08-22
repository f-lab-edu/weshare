package com.flab.batch.paymentBatch.job.steps.updatePayResultFlow.sucessFlow;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.flab.batch.CustomReader.ZeroOffsetJpaPagingItemReader;
import com.flab.batch.CustomReader.ZeroOffsetJpaPagingItemReaderBuilder;
import com.flab.batch.paymentBatch.PayJobParameter;
import com.flab.core.entity.PartyCapsule;
import com.flab.core.entity.PartyExtension;
import com.flab.core.entity.PayResultStatus;
import com.flab.core.entity.Payment;
import com.flab.core.infra.PartyExtensionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class UpdateSuccessPaymentStep {
	public static final String STEP_NAME = "updateSuccessPayment";

	@Value("${batch.pay.chunksize}")
	private int CHUNKSIZE;
	private final PayJobParameter parameter;
	private final PartyExtensionRepository partyExtensionRepository;

	@Bean
	public Step updateSuccessPayment(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder(STEP_NAME, jobRepository)
			.<Payment, Payment>chunk(CHUNKSIZE, transactionManager)
			.reader(successPaymentReader())
			.writer(successPaymentWriter())
			.build();
	}

	@Bean
	@StepScope
	public ZeroOffsetJpaPagingItemReader<Payment> successPaymentReader() {
		return new ZeroOffsetJpaPagingItemReaderBuilder<Payment>()
			.name("successPaymentReader")
			.entityClass(Payment.class)
			.queryString(
				"select p from Payment p join fetch p.partyCapsule pc where p.payDate=:payDate and p.paymentResult.payResultStatus=:payStatus")
			.parameterValues(Map.of("payDate", parameter.getPayJobDate(), "payStatus", PayResultStatus.SUCCESS))
			.pageSize(CHUNKSIZE)
			.build();
	}

	@Bean
	@StepScope
	public ItemWriter<Payment> successPaymentWriter() {
		return payments -> {
			for (Payment payment : payments) {
				PartyCapsule target = payment.getPartyCapsule();
				LocalDate previousDate = target.getExpirationDate();
				payment.getPartyCapsule().changeExpirationDate(parameter.getRenewExpirationDate());
				PartyExtension createdPartyExtension = PartyExtension.builder()
					.partyCapsule(payment.getPartyCapsule())
					.previousExpirationDate(previousDate)
					.payment(payment)
					.renewExpirationDate(parameter.getRenewExpirationDate())
					.build();
				partyExtensionRepository.save(createdPartyExtension);
			}
		};
	}
}
