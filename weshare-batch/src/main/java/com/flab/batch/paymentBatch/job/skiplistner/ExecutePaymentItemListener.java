package com.flab.batch.paymentBatch.job.skiplistner;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import com.flab.batch.paymentBatch.job.PayResultCacheFileManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ExecutePaymentItemListener implements StepExecutionListener {
	private final PayResultCacheFileManager payResultCacheFileManager;

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		payResultCacheFileManager.flushCache();
		return StepExecutionListener.super.afterStep(stepExecution);
	}
}
