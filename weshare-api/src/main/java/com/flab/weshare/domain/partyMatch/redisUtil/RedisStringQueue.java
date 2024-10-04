package com.flab.weshare.domain.partyMatch.redisUtil;

import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;

public class RedisStringQueue {
	protected final RedisTemplate<String, String> redisTemplate;

	public RedisStringQueue(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void enqueue(String e, String key) {
		redisTemplate.opsForList().leftPush(key, e);
	}

	public Optional<String> dequeue(String key) {
		return Optional.ofNullable(redisTemplate.opsForList().leftPop(key));
	}

	// 큐의 크기를 반환하는 메소드
	public boolean isEmpty(String key) {
		return redisTemplate.opsForList().size(key) == 0;
	}
}
