package com.flab.weshare.domain.partyjoin;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.core.entity.PartySign;
import com.flab.weshare.domain.base.BaseServiceTest;
import com.flab.weshare.domain.partyMatch.redisUtil.RedisPartyJoinManager;
import com.flab.weshare.domain.partyMatch.service.PartyJoinErrorHandler;
import com.flab.weshare.domain.partyMatch.service.PartyJoinListener;
import com.flab.weshare.domain.partyMatch.service.PartyJoinMessage;
import com.flab.weshare.domain.partyMatch.service.PartyMatcher;
import com.flab.weshare.domain.partyMatch.service.dto.Found;
import com.flab.weshare.domain.partyMatch.service.dto.PartyJoinLuaResult;
import com.flab.weshare.domain.partyMatch.service.dto.Queued;
import com.flab.weshare.domain.partyMatch.service.exception.EmptyPartyCapsuleNotFoundException;

public class PartyJoinListenerTest extends BaseServiceTest {
	@Mock
	private RedisTemplate<String, String> redisTemplate;
	@Mock
	private ObjectMapper objectMapper;
	@Mock
	private PartyJoinErrorHandler partyJoinErrorHandler;
	@Mock
	private RedisPartyJoinManager partyJoinManager;
	@Mock
	private PartyMatcher partyMatcher;

	@InjectMocks
	private PartyJoinListener partyJoinListener;

	@Mock
	private PartySign partySign;

	@Test
	@DisplayName("handlePartySlotAvailable processes FOUND status and calls partyMatcher")
	void handlePartySlotAvailableProcessesFoundStatus() throws Exception {
		PartyJoinMessage partyJoinMessage = new PartyJoinMessage("3", "1");
		when(redisTemplate.execute(any(RedisScript.class), anyList(), eq("3"), eq("1"))).thenReturn(
			"{\"status\":\"FOUND\",\"partyJoinId\":3,\"userId\":2}"
		);
		when(objectMapper.readValue(anyString(), eq(PartyJoinLuaResult.class))).thenReturn(
			new Found("FOUND", "3", "2")
		);
		when(partyMatcher.partyMatch(1L, 2L, 3L)).thenReturn(partySign);

		partyJoinListener.handlePartySlotAvailable(partyJoinMessage);
		verify(partyJoinManager, never()).enqueueWaitingQueue(1L, 3L, 2L);
	}

	@Test
	@DisplayName("handlePartySlotAvailable handles EmptyPartyCapsuleNotFoundException and enqueues again")
	void handlePartySlotAvailableHandlesEmptyPartyCapsuleNotFoundException() throws Exception {
		PartyJoinMessage partyJoinMessage = new PartyJoinMessage("3", "1");
		when(redisTemplate.execute(any(RedisScript.class), anyList(), eq("3"), eq("1"))).thenReturn(
			"{\"status\":\"FOUND\",\"partyJoinId\":3,\"userId\":2}"
		);
		when(objectMapper.readValue(anyString(), eq(PartyJoinLuaResult.class))).thenReturn(
			new Found("FOUND", "3", "2")
		);
		when(partyMatcher.partyMatch(1L, 2L, 3L)).thenThrow(new EmptyPartyCapsuleNotFoundException("error"));
		partyJoinListener.handlePartySlotAvailable(partyJoinMessage);
	}

	@Test
	@DisplayName("getPartyJoinLuaResult returns deserialized result from redis")
	void getPartyJoinLuaResultReturnsDeserializedResult() throws IOException {
		PartyJoinMessage partyJoinMessage = new PartyJoinMessage("3", "1");
		when(redisTemplate.execute(any(RedisScript.class), anyList(), eq("3"), eq("1"))).thenReturn(
			"{\"status\":\"QUEUED\",\"partyJoinId\":3}"
		);
		when(objectMapper.readValue(anyString(), eq(PartyJoinLuaResult.class))).thenReturn(
			new Queued("QUEUED", "3")
		);

		partyJoinListener.handlePartySlotAvailable(partyJoinMessage);
		verify(partyMatcher, never()).partyMatch(anyLong(), anyLong(), anyLong());
		verify(partyJoinManager, never()).enqueueWaitingQueue(anyLong(), anyLong(), anyLong());
	}
}
