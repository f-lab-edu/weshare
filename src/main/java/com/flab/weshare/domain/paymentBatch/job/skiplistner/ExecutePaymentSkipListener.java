package com.flab.weshare.domain.paymentBatch.job.skiplistner;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.batch.core.SkipListener;

import com.flab.weshare.domain.pay.entity.Payment;
import com.flab.weshare.domain.paymentBatch.PayResult;
import com.flab.weshare.domain.paymentBatch.job.PayResultCacheFileManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ExecutePaymentSkipListener implements SkipListener<Payment, Future<PayResult>> {
	private final PayResultCacheFileManager payResultCacheFileManager;

	@Override
	public void onSkipInWrite(Future<PayResult> item, Throwable t) {
		try {
			PayResult payResult = item.get();
			payResultCacheFileManager.add(payResult);
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
}
