package com.flab.mail.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.mail.mail.service.MailSubService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {
	@Value("${spring.data.redis.port}")
	private int port;

	@Value("${spring.data.redis.host}")
	private String host;

	private final MailSubService mailSubService;
	private final ObjectMapper objectMapper;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(host, port);
	}

	@Bean
	public MessageListenerAdapter handleSuccessPartyExtension() {
		return new MessageListenerAdapter(mailSubService, "handleSuccessPartyExtension");
	}

	@Bean
	public MessageListenerAdapter handleOttAccountInfo() {
		return new MessageListenerAdapter(mailSubService, "handleOttAccountInfo");
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory());   //connection
		redisTemplate.setKeySerializer(new StringRedisSerializer());    // key
		redisTemplate.setValueSerializer(
			new Jackson2JsonRedisSerializer<>(objectMapper, String.class)); //Java Obj <-> JSON -> String Value
		return redisTemplate;
	}

	@Bean
	public RedisMessageListenerContainer redisMessageListener(ThreadPoolTaskExecutor asyncExecutor) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(redisConnectionFactory());
		container.setTaskExecutor(asyncExecutor);
		container.addMessageListener(handleSuccessPartyExtension(), ChannelTopic.of("mail-party-extension"));
		container.addMessageListener(handleOttAccountInfo(), ChannelTopic.of("mail-ott-account-info"));
		return container;
	}
}
