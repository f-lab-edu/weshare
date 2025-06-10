package com.flab.weshare.domain.partyMatch.service;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.flab.weshare.config.RabbitMQContainerConfig;
import com.flab.weshare.config.RedisTestContainerConfig;
import com.flab.weshare.config.TestContainerConfig;
import com.flab.weshare.domain.partyMatch.redisUtil.RedisPartyJoinManager;
import com.flab.weshare.domain.partyMatch.service.dto.EmptyCapsuleExceptionErrorHandleObject;

@ActiveProfiles(value = "test")
@Transactional
@ExtendWith({RedisTestContainerConfig.class})
@Import({TestContainerConfig.class, RabbitMQContainerConfig.class})
@SpringBootTest
class PartyJoinErrorHandlerTest {
	@Autowired
	PartyJoinErrorHandler partyJoinErrorHandler;
	@Autowired
	RedisPartyJoinManager redisPartyJoinManager;
	@MockBean
	EmptyCapsulePublisher emptyCapsulePublisher;

	@Test
	void handleIfNoEmptyPartyCapsuleWaiting() {
		String ottId = "1";
		String partyJoinId = "2";
		String userId = "3";

		EmptyCapsuleExceptionErrorHandleObject handleObject = new EmptyCapsuleExceptionErrorHandleObject(
			ottId, partyJoinId, userId
		);
		partyJoinErrorHandler.handleEmptyPartyCapsuleNotFoundException(handleObject);
		verify(emptyCapsulePublisher, never()).publishPartySlotAvailable(any(PartyJoinMessage.class));
	}

	@Test
	void handleIfEmptyPartyCapsuleWaiting() {
		//given
		String ottId = "1";
		String partyJoinId = "2";
		String userId = "3";

		EmptyCapsuleExceptionErrorHandleObject handleObject = new EmptyCapsuleExceptionErrorHandleObject(
			ottId, partyJoinId, userId
		);

		redisPartyJoinManager.enqueueEmptyCapsuleQueue(ottId, partyJoinId);
		//when
		partyJoinErrorHandler.handleEmptyPartyCapsuleNotFoundException(handleObject);
		//then
		verify(emptyCapsulePublisher, times(1)).publishPartySlotAvailable(any(PartyJoinMessage.class));
	}

}
