package com.flab.mail.mail.service;

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

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {
	public static final String SUCCESS_PARTY_EXTENSION_QUEUE = "mail-party-extension";
	public static final String OTT_ACCOUNT_INFO_QUEUE = "mail-ott-account-info";
	public static final String SUCCESS_PARTY_JOIN_QUEUE = "mail-party-join";

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
	public MessageListenerAdapter handlePartyJoin() {
		return new MessageListenerAdapter(mailSubService, "handlePartyJoin");
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
		container.addMessageListener(handleSuccessPartyExtension(), ChannelTopic.of(SUCCESS_PARTY_EXTENSION_QUEUE));
		container.addMessageListener(handleOttAccountInfo(), ChannelTopic.of(OTT_ACCOUNT_INFO_QUEUE));
		container.addMessageListener(handlePartyJoin(), ChannelTopic.of(SUCCESS_PARTY_JOIN_QUEUE));
		return container;
	}
}
