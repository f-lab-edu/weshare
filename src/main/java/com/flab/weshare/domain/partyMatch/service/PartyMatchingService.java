package com.flab.weshare.domain.partyMatch.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.flab.weshare.domain.party.entity.Ott;
import com.flab.weshare.domain.party.entity.PartyCapsule;
import com.flab.weshare.domain.party.entity.PartyJoin;
import com.flab.weshare.domain.party.repository.PartyCapsuleRepository;
import com.flab.weshare.domain.party.repository.PartyJoinRepository;
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
