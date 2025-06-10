package com.flab.weshare.domain.partyMatch.service;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.core.entity.PartySign;
import com.flab.core.infra.PartyJoinRepository;
import com.flab.weshare.config.RabbitMQConfig;
import com.flab.weshare.domain.partyMatch.redisUtil.RedisPartyJoinManager;
import com.flab.weshare.domain.partyMatch.service.dto.Found;
import com.flab.weshare.domain.partyMatch.service.dto.PartyJoinLuaResult;
import com.flab.weshare.domain.partyMatch.service.dto.Queued;
import com.flab.weshare.domain.partyMatch.service.exception.EmptyPartyCapsuleNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("local")
@Component
@RequiredArgsConstructor
public class PartyJoinListener {
	public static final String QUEUED = "QUEUED";
	public static final String FOUND = "FOUND";
	private static final String luaScript =
		"""
			local empty_slot_key = KEYS[1]
			local waiting_queue_key = KEYS[2]
			local party_capsule_id = ARGV[1]
			local ott_id = ARGV[2]
			local input = redis.call('RPOP', waiting_queue_key .. ":" .. ott_id)
			
			if input then
			    local parts = {}
			    for part in string.gmatch(input, "([^|]+)") do
			        table.insert(parts, part)
			    end
			    return "{\\"status\\":\\"FOUND\\",\\"partyJoinId\\":" .. parts[1] .. ",\\"userId\\":" .. parts[2] .. "}"
			else
			    redis.call('LPUSH', empty_slot_key .. ":" .. ott_id, party_capsule_id)
			    return "{\\"status\\":\\"QUEUED\\",\\"partySlotId\\":" .. party_capsule_id .. "}"
			end
			""";

	private static final List<String> KEYS = List.of(
		RedisPartyJoinManager.EMPTY_CAPSULE_QUEUE_KEY,
		RedisPartyJoinManager.WAITING_QUEUE_KEY
	);

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;
	private final PartyJoinErrorHandler partyJoinErrorHandler;
	private final PartyJoinRepository partyJoinRepository;
	private final RedisPartyJoinManager partyJoinManager;
	private final PartyMatcher partyMatcher;

	@RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
	public void handlePartySlotAvailable(PartyJoinMessage message) {
		try {
			log.info("message consume : {}", message);
			PartyJoinLuaResult partyJoinLuaResult = getPartyJoinLuaResult(message);
			if (partyJoinLuaResult instanceof Found found) { //현재 조건에 부합하는 파티 가입 요청자 찾음
				try {
					PartySign partySign = partyMatcher.partyMatch(Long.valueOf(message.ottId()),
						Long.valueOf(found.getUserId()),
						Long.valueOf(message.partySlotId()));
					log.info("파티 매칭 완료 partysignId : {} (partyCapsule : {}, partyJoin : {})", partySign.getId(),
						message.partySlotId(), found.getPartyJoinId());
				} catch (EmptyPartyCapsuleNotFoundException e) {

				}
			} else if (partyJoinLuaResult instanceof Queued queued) {
				log.info("파티 캡슐이 waiting Queue 에 등록됨 : {}", queued.getPartySlotId());
			}
		} catch (Exception e) {
			log.error("message consume error : {}", e.getMessage());
		}
	}

	private PartyJoinLuaResult getPartyJoinLuaResult(PartyJoinMessage message) throws JsonProcessingException {
		DefaultRedisScript<String> redisScript = new DefaultRedisScript<>(luaScript, String.class);
		String result = redisTemplate.execute(
			redisScript,
			KEYS,
			message.partySlotId(),
			message.ottId()
		);
		log.info("result : {}", result);
		return objectMapper.readValue(result, PartyJoinLuaResult.class);
	}
}
