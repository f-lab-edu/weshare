package com.flab.weshare.domain.paymentBatch;

import java.time.Instant;
import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PayScheduler {
	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job paymentJob;

	//@Scheduled(cron = "0/10 * * * * *")
	//@Scheduled(cron = "0 0 0/1 * * *") //1시간 마다
	public void partyMatchingSchedule() {
		try {
			JobParameters parameters = new JobParametersBuilder()
				.addDate("executionDate", Date.from(Instant.now()))
				.toJobParameters();

			jobLauncher.run(paymentJob, parameters);
		} catch (Exception e) {
			log.error("배치 잡 실행시작 중 예외 발생", e);
		}
	}
}
