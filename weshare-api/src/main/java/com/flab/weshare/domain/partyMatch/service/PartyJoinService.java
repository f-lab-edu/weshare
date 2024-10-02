package com.flab.weshare.domain.partyMatch.service;

import java.io.IOException;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.flab.core.entity.PartyJoin;
import com.flab.core.entity.PartySign;
import com.flab.core.infra.OttRepository;
import com.flab.core.infra.PartyJoinRepository;
import com.flab.core.infra.UserRepository;
import com.flab.weshare.domain.partyMatch.redisUtil.RedisPartyJoinManager;
import com.flab.weshare.domain.partyMatch.service.dto.EmptyCapsuleExceptionErrorHandleObject;
import com.flab.weshare.domain.partyMatch.service.exception.EmptyPartyCapsuleNotFoundException;
import com.flab.weshare.domain.partyMatch.service.wrapper.Accepted;
import com.flab.weshare.domain.partyMatch.service.wrapper.JoinResult;
import com.flab.weshare.domain.partyMatch.service.wrapper.Queued;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartyJoinService {
	private final RedisPartyJoinManager partyJoinManager;
	private final OttRepository ottRepository;
	private final UserRepository userRepository;
	private final PartyJoinRepository partyJoinRepository;
	private final PartyMatcher partyMatcher;
	private final PartyJoinErrorHandler partyJoinErrorHandler;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public JoinResult processJoinRequest(final Long ottId, final Long userId) throws IOException {
		Optional<String> dequeud = partyJoinManager.dequeueEmptyCapsuleQueue(ottId);
		PartyJoin createdPartyjoin = createPartyJoin(ottId, userId);

		if (dequeud.isPresent()) {
			try {
				PartySign partySign = partyMatcher.partyMatch(ottId, userId, Long.parseLong(dequeud.get()));
				log.info("파티 가입 요청 - 즉시 매칭 완료 party sign : {}, partyJoin : {}, partyCapsule : {}", partySign.getId(),
					partySign.getPayRes().getId(), partySign.getPartyCapsule().getId());
				return new Accepted(partySign.getPartyCapsule());
			} catch (EmptyPartyCapsuleNotFoundException e) {
				eventPublisher.publishEvent(new EmptyCapsuleExceptionErrorHandleObject(
					String.valueOf(ottId),
					String.valueOf(createdPartyjoin.getId()),
					String.valueOf(userId)
				));

				log.info("빈 파티 캡슐 조회 중 예외가 발생하여 핸들러 작동하기전 대기열 추가 등록 응답 반환");
				return new Queued(createdPartyjoin);
			}
		}
		log.info("현재 {}에 대기 중인 빈자리 없음", ottId);
		return new Queued(createdPartyjoin);

	}

	private PartyJoin createPartyJoin(Long ottId, Long userId) {
		PartyJoin partyJoin = PartyJoin.generateWaitingPartyJoin(userRepository.getReferenceById(userId),
			ottRepository.getReferenceById(ottId));
		partyJoinRepository.save(partyJoin);
		return partyJoin;
	}
}
