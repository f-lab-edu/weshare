package com.flab.weshare.domain.paymentBatch.job;

import java.util.Collections;
import java.util.concurrent.Future;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import com.flab.weshare.domain.pay.entity.Payment;
import com.flab.weshare.domain.pay.entity.PaymentStatus;
import com.flab.weshare.domain.pay.repository.PaymentRepository;
import com.flab.weshare.domain.pay.service.CardService;
import com.flab.weshare.domain.paymentBatch.PayResult;
import com.flab.weshare.domain.paymentBatch.PayResultRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ExecutePaymentStepConfiguration {
	@Value("${batch.pay.chunksize}")
	private int CHUNKSIZE;
	private final PayResultRepository payResultRepository;
	private final PaymentRepository paymentRepository;
	private final CardService cardService;

	@Bean
	@JobScope
	public Step payStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("payStep", jobRepository)
			.<Payment, Future<PayResult>>chunk(CHUNKSIZE, transactionManager)
			.reader(waitingPaymentItemReader())
			.processor(asyncItemProcessor())
			.writer(asyncItemWriter())
			.faultTolerant()
			.processorNonTransactional()
			.skip(Exception.class)
			.skipLimit(2)
			.build();
	}

	@Bean
	@StepScope
	public RepositoryItemReader<Payment> waitingPaymentItemReader() {
		return new RepositoryItemReaderBuilder<Payment>()
			.name("waitingPaymentItemReader")
			.repository(paymentRepository)
			.methodName("findFetchPagePaymentByStatus")
			.arguments(PaymentStatus.WAITING)
			.pageSize(CHUNKSIZE)
			.sorts(Collections.singletonMap("createdDate", Sort.Direction.ASC))
			.build();
	}

	@Bean
	@StepScope
	public AsyncItemProcessor<Payment, PayResult> asyncItemProcessor() {
		final AsyncItemProcessor<Payment, PayResult> processor = new AsyncItemProcessor<>();
		processor.setDelegate(delegateProcessor());
		processor.setTaskExecutor(new SimpleAsyncTaskExecutor());
		return processor;
	}

	@Bean
	@StepScope
	public ItemProcessor<Payment, PayResult> delegateProcessor() {
		return payment -> {
			log.info("결제요청 processor {}번 payment", payment.getId());
			return cardService.payRequest(payment);
		};
	}

	@Bean
	@StepScope
	public AsyncItemWriter<PayResult> asyncItemWriter() {
		final AsyncItemWriter<PayResult> writer = new AsyncItemWriter<>();
		writer.setDelegate(delegateWriter());
		return writer;
	}

	@Bean
	@StepScope
	public RepositoryItemWriter<PayResult> delegateWriter() {
		return new RepositoryItemWriterBuilder<PayResult>()
			.repository(payResultRepository)
			.methodName("save")
			.build();
	}
}
