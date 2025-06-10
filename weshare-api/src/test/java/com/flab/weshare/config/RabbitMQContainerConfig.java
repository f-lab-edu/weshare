package com.flab.weshare.config;

import java.time.Duration;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class RabbitMQContainerConfig {
	@Container
	static GenericContainer<?> rabbitMQ = new GenericContainer<>("rabbitmq:3-management")
		.withExposedPorts(5672, 15672)
		.withEnv("RABBITMQ_DEFAULT_USER", "testuser")
		.withEnv("RABBITMQ_DEFAULT_PASS", "testpass")
		.waitingFor(Wait.forHttp("/api/healthchecks/node")  // 관리 UI 기준 헬스체크
			.forPort(15672)
			.forStatusCode(200)
			.withStartupTimeout(Duration.ofSeconds(3000)));

	@DynamicPropertySource
	static void overrideProps(DynamicPropertyRegistry registry) {
		registry.add("spring.rabbitmq.host", rabbitMQ::getHost);
		registry.add("spring.rabbitmq.port", () -> rabbitMQ.getMappedPort(5672));
		registry.add("spring.rabbitmq.username", () -> "testuser");
		registry.add("spring.rabbitmq.password", () -> "testpass");
	}
}
