package com.flab.weshare.config;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class AsyncConfiguration implements AsyncConfigurer {
	@Value(value = "async.prefix")
	private static String threadNamePrefix;

	@Value(value = "async.corePoolSize")
	private static int corePoolSize;

	@Value(value = "async.maxPoolSize")
	private static int maxPoolSize;

	@Value(value = "async.queueCapacity")
	private static int queueCapacity;

	@Value(value = "async.keepAliveSeconds")
	private static int keepAliveSeconds;

	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setThreadNamePrefix(threadNamePrefix);
		threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
		threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
		threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
		threadPoolTaskExecutor.setKeepAliveSeconds(keepAliveSeconds);
		threadPoolTaskExecutor.initialize();
		return threadPoolTaskExecutor;
	}
}
