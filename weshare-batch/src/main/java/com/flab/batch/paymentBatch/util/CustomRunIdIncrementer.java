package com.flab.batch.paymentBatch.util;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;

public class CustomRunIdIncrementer extends RunIdIncrementer {
	private static final String RUN_ID = "run.id";

	@Override
	public JobParameters getNext(JobParameters parameters) {
		JobParameters params = (parameters == null) ? new JobParameters() : parameters;
		return new JobParametersBuilder()
			.addJobParameters(parameters)
			.addLong(RUN_ID, params.getLong(RUN_ID, 0L) + 1)
			.toJobParameters();
	}
}
