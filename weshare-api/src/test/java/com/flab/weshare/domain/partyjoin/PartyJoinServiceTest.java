package com.flab.weshare.domain.partyjoin;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.flab.core.entity.PartyCapsule;
import com.flab.core.entity.PartyJoin;
import com.flab.core.entity.PartyJoinStatus;
import com.flab.core.entity.PartySign;
import com.flab.core.entity.PayRes;
import com.flab.core.infra.OttRepository;
import com.flab.core.infra.PartyJoinRepository;
import com.flab.core.infra.UserRepository;
import com.flab.weshare.domain.base.BaseServiceTest;
import com.flab.weshare.domain.partyMatch.redisUtil.RedisPartyJoinManager;
import com.flab.weshare.domain.partyMatch.service.PartyJoinService;
import com.flab.weshare.domain.partyMatch.service.PartyMatcher;
import com.flab.weshare.domain.partyMatch.service.exception.EmptyPartyCapsuleNotFoundException;
import com.flab.weshare.domain.partyMatch.service.wrapper.Accepted;
import com.flab.weshare.domain.partyMatch.service.wrapper.JoinResult;
import com.flab.weshare.domain.partyMatch.service.wrapper.Queued;

class PartyJoinServiceTest extends BaseServiceTest {
	@Mock
	private RedisPartyJoinManager partyJoinManager;
	@Mock
	private OttRepository ottRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private PartyJoinRepository partyJoinRepository;
	@Mock
	private PartyMatcher partyMatcher;
	@Mock
	private PartySign partySign;
	@Mock
	private PartyCapsule partyCapsule;
	@Mock
	private PayRes payRes;

	@InjectMocks
	private PartyJoinService partyJoinService;

	@Test
	@DisplayName("만약 현재 빈자리가 있다면 파티매칭 정상 수행후 Accepted 반환")
	void processJoinRequestReturnsAcceptedOnSuccess() throws IOException {
		Long ottId = 1L;
		Long userId = 2L;
		String capsuleId = "10";
		when(partyJoinManager.dequeueEmptyCapsuleQueue(ottId)).thenReturn(Optional.ofNullable(capsuleId));
		when(partyMatcher.partyMatch(ottId, userId, Long.parseLong(capsuleId))).thenReturn(partySign);
		when(partySign.getPartyCapsule()).thenReturn(partyCapsule);
		when(partySign.getPayRes()).thenReturn(payRes);

		JoinResult result = partyJoinService.processJoinRequest(ottId, userId);

		assertThat(result).isInstanceOf(Accepted.class);
	}

	@Test
	@DisplayName("만약 현재 빈자리가 있으나 파티매칭 중 빈자리에 문제가 발생했다면 요청을 대기열에 넣고 Queued 반환")
	void processJoinRequestReturnsQueuedWhenCapsuleNotFound() throws IOException {
		Long ottId = 1L;
		Long userId = 2L;
		String capsuleId = "10";
		when(partyJoinManager.dequeueEmptyCapsuleQueue(ottId)).thenReturn(Optional.ofNullable(capsuleId));
		when(partyMatcher.partyMatch(ottId, userId, Long.parseLong(capsuleId)))
			.thenThrow(new EmptyPartyCapsuleNotFoundException("Party capsule not found"));

		PartyJoin partyJoin = PartyJoin.builder()
			.partyJoinStatus(PartyJoinStatus.WAITING)
			.ott(null)
			.user(null)
			.build();
		when(ottRepository.getReferenceById(ottId)).thenReturn(null);
		when(userRepository.getReferenceById(userId)).thenReturn(null);
		when(partyJoinRepository.save(any())).thenReturn(partyJoin);

		JoinResult result = partyJoinService.processJoinRequest(ottId, userId);

		assertThat(result).isInstanceOf(Queued.class);
		verify(partyJoinManager, times(1)).enqueueWaitingQueue(ottId, partyJoin.getId(), userId);
	}

	@Test
	@DisplayName("만약 현재 빈자리가 없으면 요청을 대기열에 넣고 Queued 반환")
	void processJoinRequestReturnsQueuedWhenNoCapsule() throws IOException {
		Long ottId = 1L;
		Long userId = 2L;
		when(partyJoinManager.dequeueEmptyCapsuleQueue(ottId)).thenReturn(Optional.empty());

		PartyJoin partyJoin = PartyJoin.builder()
			.partyJoinStatus(PartyJoinStatus.WAITING)
			.ott(null)
			.user(null)
			.build();
		when(ottRepository.getReferenceById(ottId)).thenReturn(null);
		when(userRepository.getReferenceById(userId)).thenReturn(null);
		when(partyJoinRepository.save(any())).thenReturn(partyJoin);

		JoinResult result = partyJoinService.processJoinRequest(ottId, userId);

		assertThat(result).isInstanceOf(Queued.class);
		verify(partyJoinManager, times(1)).enqueueWaitingQueue(ottId, partyJoin.getId(), userId);
	}
}
