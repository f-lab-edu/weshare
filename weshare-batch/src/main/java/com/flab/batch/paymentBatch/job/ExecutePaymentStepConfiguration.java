package com.flab.batch.paymentBatch.job;

import java.util.Collections;
import java.util.concurrent.Executor;
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
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import com.flab.batch.paymentBatch.job.skiplistner.ExecutePaymentItemListener;
import com.flab.batch.paymentBatch.job.skiplistner.ExecutePaymentSkipListener;
import com.flab.core.entity.PayResult;
import com.flab.core.entity.PayResultStatus;
import com.flab.core.entity.Payment;
import com.flab.core.entity.PaymentStatus;
import com.flab.core.infra.PayResultRepository;
import com.flab.core.infra.PaymentRepository;
import com.flab.wesharepay.exception.CommonPayServiceException;
import com.flab.wesharepay.service.PayServiceImpl;
import com.flab.wesharepay.service.Receipt;

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
	private final PayServiceImpl payService;
	private final Executor asyncExecutor;
	private final PayResultCacheFileManager payResultCacheFileManager;

	@Bean
	@JobScope
	public Step payStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("payStep", jobRepository).<Payment, Future<PayResult>>chunk(CHUNKSIZE,
				transactionManager)
			.reader(waitingPaymentItemReader())
			.processor(asyncItemProcessor())
			.writer(asyncItemWriter())
			.faultTolerant()
			.processorNonTransactional()
			.skip(DataAccessException.class)
			.skipLimit(Integer.MAX_VALUE)
			.noRetry(CommonPayServiceException.class) //결제 서비스 이상 예외는 재시도 및 스킵 하지않고 배치 잡 종료.
			.listener(executePaymentSkipListener())
			.listener(executePaymentItemListener())
			.build();
	}

	@Bean
	@StepScope
	public RepositoryItemReader<Payment> waitingPaymentItemReader() {
		return new RepositoryItemReaderBuilder<Payment>().name("waitingPaymentItemReader")
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
		processor.setTaskExecutor((TaskExecutor)asyncExecutor);
		return processor;
	}

	@Bean
	@StepScope
	public ItemProcessor<Payment, PayResult> delegateProcessor() {
		return payment -> {
			log.info("payment 결제요청 {}", payment.getId());
			return buildPayResult(payment,
				payService.payRequest(payment.getCard().getBillingKey(), payment.getAmount().getIntegerAmount(),
					payment.getId()));
		};
	}

	private PayResult buildPayResult(Payment payment, Receipt receipt) {
		PayResultStatus payResultStatus;
		if (receipt.isSuccess()) {
			return PayResult.ofSuccessfulPayResult(payment, receipt.getReceipt().toString());
		} else {
			return PayResult.ofRejectedPayResult(payment, receipt.getReceipt().toString());
		}
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
		return new RepositoryItemWriterBuilder<PayResult>().repository(payResultRepository).methodName("save").build();
	}

	@Bean
	@StepScope
	public ExecutePaymentSkipListener executePaymentSkipListener() {
		return new ExecutePaymentSkipListener(payResultCacheFileManager);
	}

	@Bean
	@StepScope
	public ExecutePaymentItemListener executePaymentItemListener() {
		return new ExecutePaymentItemListener(payResultCacheFileManager);
	}
}
