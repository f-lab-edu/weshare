package com.flab.batch.paymentBatch.job.steps;

import java.util.List;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.flab.core.entity.Ott;
import com.flab.core.infra.OttRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class OttReadStepConfiguration {
	private final OttMemoryCache ottMemoryCache;
	private final OttRepository ottRepository;

	@Bean
	@StepScope
	public Tasklet ottReadTasklet() {
		return (contribution, chunkContext) -> {
			final List<Ott> foundOtt = ottRepository.findAll();
			foundOtt.forEach(ottMemoryCache::addOtt);
			return RepeatStatus.FINISHED;
		};
	}

	@Bean
	@JobScope
	public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("ottReadTasklet", jobRepository)
			.tasklet(ottReadTasklet(), transactionManager)
			.build();
	}
}
