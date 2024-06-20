package com.flab.weshare.domain.partyMatch.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.flab.core.entity.Ott;
import com.flab.core.entity.PartyCapsule;
import com.flab.core.entity.PartyJoin;
import com.flab.core.infra.PartyCapsuleRepository;
import com.flab.core.infra.PartyJoinRepository;
import com.flab.weshare.domain.party.service.PartyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartyMatchingService {
	private final PartyService partyService;
	private final PartyCapsuleRepository partyCapsuleRepository;
	private final PartyJoinRepository partyJoinRepository;

	@Async
	public CompletableFuture<Long> partyMatch(final Ott ott) {
		List<PartyCapsule> partyCapsules = partyCapsuleRepository.findEmptyCapsuleByOtt(ott);
		List<PartyJoin> partyJoins = partyJoinRepository.findWaitingPartyJoinByOtt(ott);
		int countMatchable = Math.min(partyCapsules.size(), partyJoins.size());

		for (int i = 0; i < countMatchable; i++) {
			PartyJoin partyJoin = partyJoins.get(i);
			PartyCapsule partyCapsule = partyCapsules.get(i);
			try {
				partyService.joinParty(partyJoin, partyCapsule);
			} catch (Exception exception) {
				log.error("파티 매칭 중 에러발생 {} ", exception.getMessage());
				log.error("partyJoin id ={}, partyCapsuleId= {} ", partyJoin.getId(), partyCapsule.getId());
			}
		}
		return CompletableFuture.completedFuture(ott.getId());
	}
}
