package com.flab.weshare.domain.partyMatch.redisUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.flab.core.infra.OttRepository;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedisPartyJoinManager {
	public static final String EMPTY_CAPSULE_QUEUE_KEY = "party:empty:capsule:queue";
	public static final String WAITING_QUEUE_KEY = "party:waiting:queue";
	public static final List<String> KEYS = List.of(EMPTY_CAPSULE_QUEUE_KEY, WAITING_QUEUE_KEY);
	private final RedisStringQueue queue;
	private final OttRepository ottRepository;

	private final Map<String, String> waitingQueueKeyMap = new HashMap<>();
	private final Map<String, String> emptyCapsuleQueueKeyMap = new HashMap<>();

	public RedisPartyJoinManager(RedisTemplate<String, String> redisTemplate, OttRepository ottRepository) {
		this.queue = new RedisStringQueue(redisTemplate);
		this.ottRepository = ottRepository;
	}

	public String getWaitingQueueKey(String ottId) {
		return waitingQueueKeyMap.get(ottId);
	}

	public String getEmptyCapsuleQueueKey(String ottId) {
		return emptyCapsuleQueueKeyMap.get(ottId);
	}

	@PostConstruct
	public void init() {
		ottRepository.findAll().forEach(ott -> {
			String ottId = String.valueOf(ott.getId());
			waitingQueueKeyMap.put(ottId, WAITING_QUEUE_KEY + ":" + ottId);
			emptyCapsuleQueueKeyMap.put(ottId, EMPTY_CAPSULE_QUEUE_KEY + ":" + ottId);
		});

		log.info("waitingQueueKeyMap initialized with OTT IDs: {}", waitingQueueKeyMap.keySet());
		log.info("emptyCapsuleQueueKeyMap initialized with OTT IDs: {}", emptyCapsuleQueueKeyMap.keySet());
	}

	public void enqueueWaitingQueue(Long ottId, Long partyJoinId, Long userId) {
		String key = waitingQueueKeyMap.get(String.valueOf(ottId));
		if (key == null) {
			throw new IllegalArgumentException("OTT ID not found in waitingQueueKeyMap");
		} else {
			queue.enqueue(String.valueOf(partyJoinId) + "|" + String.valueOf(userId), key);
		}
	}

	public void enqueueEmptyCapsuleQueue(String ottId, String partyJoinId) {
		String key = emptyCapsuleQueueKeyMap.get(ottId);
		if (key == null) {
			throw new IllegalArgumentException("OTT ID not found in emptyCapsuleQueueKeyMap");
		} else {
			queue.enqueue(partyJoinId, key);
		}
	}

	public Optional<String> dequeueWaitingQueue(Long ottId) {
		String key = waitingQueueKeyMap.get(String.valueOf(ottId));
		if (key == null) {
			throw new IllegalArgumentException("OTT ID not found in waitingQueueKeyMap");
		} else {
			return queue.dequeue(key);
		}
	}

	public Optional<String> dequeueEmptyCapsuleQueue(Long ottId) {
		log.info("param key = {}", ottId);
		String key = emptyCapsuleQueueKeyMap.get(String.valueOf(ottId));
		if (key == null) {
			throw new IllegalArgumentException("OTT ID not found in emptyCapsuleQueueKeyMap");
		} else {
			log.info("key = {}", key);

			return queue.dequeue(key);
		}
	}
}
