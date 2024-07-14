package com.flab.weshare.config;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class RedisTestContainerConfig implements BeforeAllCallback {
	private static final String REDIS_IMAGE = "redis:alpine";
	private static final int REDIS_PORT = 6379;
	private GenericContainer REDIS_CONTAINER;

	@Override
	public void beforeAll(ExtensionContext context) {
		REDIS_CONTAINER = new GenericContainer(DockerImageName.parse(REDIS_IMAGE))
			.withExposedPorts(REDIS_PORT);
		REDIS_CONTAINER.start();
		System.setProperty("spring.data.redis.host", REDIS_CONTAINER.getHost());
		System.setProperty("spring.data.redis.port", String.valueOf(REDIS_CONTAINER.getMappedPort(REDIS_PORT
		)));
	}
}
