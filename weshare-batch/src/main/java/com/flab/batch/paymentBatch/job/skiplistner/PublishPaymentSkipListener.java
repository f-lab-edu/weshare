package com.flab.batch.paymentBatch.job.skiplistner;

import org.springframework.batch.core.listener.SkipListenerSupport;

import com.flab.core.entity.PartyCapsule;
import com.flab.core.entity.Payment;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PublishPaymentSkipListener extends SkipListenerSupport<PartyCapsule, Payment> {
	@Override
	public void onSkipInProcess(PartyCapsule item, Throwable t) {
		log.error("payment 발행중 에러발생 {}", item.getId(), t);
	}
}
