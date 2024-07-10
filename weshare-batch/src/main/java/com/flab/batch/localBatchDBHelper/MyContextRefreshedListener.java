package com.flab.batch.localBatchDBHelper;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Profile("data-generate")
@Component
@RequiredArgsConstructor
public class MyContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {
	private final BatchDataGenerator batchDataGenerator;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		batchDataGenerator.generateData();
	}
}
