package com.flab.weshare.domain.partyMatch.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.flab.weshare.domain.partyMatch.redisUtil.RedisPartyJoinManager;
import com.flab.weshare.domain.partyMatch.service.dto.EmptyCapsuleExceptionErrorHandleObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartyJoinErrorHandler {
	private final RedisTemplate<String, String> redisTemplate;
	private final EmptyCapsulePublisher emptyCapsulePublisher;
	private static final String luaScript =
		"""
			local empty_slot_key = KEYS[1]
			   local waiting_queue_key = KEYS[2]
			   local party_join_id = ARGV[1]
			   local ott_id = ARGV[2]
			   local user_id = ARGV[3]
			
			   redis.call('RPUSH', waiting_queue_key .. ":" .. ott_id, party_join_id .. "|" .. user_id)
			   local input = redis.call('RPOP', empty_slot_key .. ":" .. ott_id)
			
			   if input then
			       return party_join_id
			   else
			       return nil;
			   end
			""";

	@Async
	@TransactionalEventListener
	public void handleEmptyPartyCapsuleNotFoundException(EmptyCapsuleExceptionErrorHandleObject errorHandleObject) {
		log.info("Handling EmptyPartyCapsuleNotFoundException for errorHandleObject: {}", errorHandleObject);
		String result = redisTemplate.execute(
			new DefaultRedisScript<>(luaScript, String.class),
			RedisPartyJoinManager.KEYS,
			errorHandleObject.partyJoinId(), errorHandleObject.ottId(), errorHandleObject.userId()
		);
		log.info("Lua script executed with result: {}", result);

		if (result != null) {
			try {
				PartyJoinMessage partyJoinMessage = new PartyJoinMessage(result, errorHandleObject.ottId());
				emptyCapsulePublisher.publishPartySlotAvailable(partyJoinMessage);
				log.info("Published party slot available message: {}", partyJoinMessage);
			} catch (Exception e) {
				log.error("Failed to publish party slot available message: {}", e.getMessage());
			}
		}
	}
}
