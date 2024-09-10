package com.flab.batch.paymentBatch.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.Setter;

@Setter
@EnableAsync
@Configuration
@ConfigurationProperties(prefix = "async")
public class AsyncConfiguration {
	private String threadNamePrefix;
	private int corePoolSize;
	private int maxPoolSize;
	private int keepAliveSeconds;

	@Bean(name = {"asyncExecutor"})
	public ThreadPoolTaskExecutor getAsyncExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setThreadNamePrefix(threadNamePrefix);
		threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
		threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
		threadPoolTaskExecutor.setKeepAliveSeconds(keepAliveSeconds);
		threadPoolTaskExecutor.setAllowCoreThreadTimeOut(true);
		threadPoolTaskExecutor.initialize();
		return threadPoolTaskExecutor;
	}
}
