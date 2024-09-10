package com.flab.batch.paymentBatch.job.steps.updatePayResultFlow.sucessFlow;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.flab.batch.CustomReader.ZeroOffsetJpaPagingItemReader;
import com.flab.batch.CustomReader.ZeroOffsetJpaPagingItemReaderBuilder;
import com.flab.batch.paymentBatch.PayJobParameter;
import com.flab.batch.paymentBatch.job.steps.preBatchStep.OttMemoryCache;
import com.flab.core.entity.EmailSentStatus;
import com.flab.core.entity.PartyExtension;
import com.flab.mail.mail.MailController;
import com.flab.mail.mail.dto.EmailResponseDto;
import com.flab.mail.mail.dto.SuccessPartyExtensionMailDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SendMailPartyExtensionStepConfiguration {
	@Value("${batch.pay.chunksize}")
	private int CHUNKSIZE;
	private final PayJobParameter parameter;
	private final OttMemoryCache ottMemoryCache;
	private final Executor asyncExecutor;
	private final MailController mailEventListener;

	@Bean
	public Step sendMailPartyExtensionStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("sendMailPartyExtensionStep", jobRepository)
			.<PartyExtension, Future<PartyExtensionMailPacker>>chunk(CHUNKSIZE, transactionManager)
			.reader(partyExtensionReader())
			.processor(asyncPartyExtensionMailProcessor())
			.writer(asyncPartyExtensionMailWriter())
			.build();
	}

	@Bean
	@StepScope
	public ZeroOffsetJpaPagingItemReader<PartyExtension> partyExtensionReader() {
		return new ZeroOffsetJpaPagingItemReaderBuilder<PartyExtension>()
			.name("partyExtensionReader")
			.entityClass(PartyExtension.class)
			.queryString(
				"select pe from PartyExtension pe join fetch pe.partyCapsule pc "
					+ "join fetch pc.partyMember "
					+ "join fetch pe.payment "
					+ "where pe.renewExpirationDate=:renewExpirationDate "
					+ "and pe.emailSendResult.emailSentStatus=:mailSentStatus")
			.parameterValues(Map.of("renewExpirationDate", parameter.getRenewExpirationDate()
				, "mailSentStatus", EmailSentStatus.NOT_SENT))
			.pageSize(CHUNKSIZE)
			.build();
	}

	@Bean
	@StepScope
	public AsyncItemProcessor<PartyExtension, PartyExtensionMailPacker> asyncPartyExtensionMailProcessor() {
		final AsyncItemProcessor<PartyExtension, PartyExtensionMailPacker> processor = new AsyncItemProcessor<>();
		processor.setDelegate(delegatePartyExtensionMailProcessor());
		processor.setTaskExecutor((TaskExecutor)asyncExecutor);
		return processor;
	}

	@Bean
	@StepScope
	public ItemProcessor<PartyExtension, PartyExtensionMailPacker> delegatePartyExtensionMailProcessor() {
		return partyExtension -> {
			log.info("partyExtension send mail  {}", partyExtension.getId());
			EmailResponseDto emailResponseDto = mailEventListener.sendSuccessPartyExtensionMail(
				new SuccessPartyExtensionMailDto(
					ottMemoryCache.getOttById(partyExtension.getPartyCapsule().getOtt().getId()).getName(),
					partyExtension.getPreviousExpirationDate(),
					partyExtension.getRenewExpirationDate(),
					parameter.getPayJobDate(),
					partyExtension.getPayment().getAmount().getIntegerAmount(),
					partyExtension.getPartyCapsule().getPartyMember().getEmail()
				));
			return new PartyExtensionMailPacker(partyExtension, emailResponseDto);
		};
	}

	@Bean
	@StepScope
	public AsyncItemWriter<PartyExtensionMailPacker> asyncPartyExtensionMailWriter() {
		final AsyncItemWriter<PartyExtensionMailPacker> writer = new AsyncItemWriter<>();
		writer.setDelegate(delegatePartyExtensionMailWriter());
		return writer;
	}

	@Bean
	@StepScope
	public ItemWriter<PartyExtensionMailPacker> delegatePartyExtensionMailWriter() {
		return partyExtensionMailPackers -> {
			for (PartyExtensionMailPacker partyExtensionMailPacker : partyExtensionMailPackers) {
				PartyExtension partyExtension = partyExtensionMailPacker.partyExtension;
				EmailResponseDto emailResponseDto = partyExtensionMailPacker.emailResponseDto;

				if (emailResponseDto.errorMessage() == null && emailResponseDto.sendAt() != null) {
					partyExtension.getEmailSendResult().successSent(emailResponseDto.sendAt());
				} else if (emailResponseDto.errorMessage() != null && emailResponseDto.sendAt() == null) {
					partyExtension.getEmailSendResult().failSent(emailResponseDto.errorMessage());
				} else {
					log.error("emailResponse Dto의 상태가 올바르지않습니다. partyExtension id = {}, {}", partyExtension.getId(),
						emailResponseDto);
					throw new IllegalArgumentException("emailResponse Dto의 상태가 올바르지않습니다.");
				}
			}
		};
	}
}
