package com.flab.weshare.domain.party.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flab.core.entity.Ott;
import com.flab.core.entity.Party;
import com.flab.core.entity.PartyCapsule;
import com.flab.core.entity.PartyCapsuleStatus;
import com.flab.core.entity.PartyJoin;
import com.flab.core.entity.User;
import com.flab.core.infra.OttRepository;
import com.flab.core.infra.PartyCapsuleRepository;
import com.flab.core.infra.PartyJoinRepository;
import com.flab.core.infra.PartyRepository;
import com.flab.core.infra.UserRepository;
import com.flab.weshare.domain.party.dto.ContractRenewalResponse;
import com.flab.weshare.domain.party.dto.LeadingPartySummary;
import com.flab.weshare.domain.party.dto.ModifyPartyRequest;
import com.flab.weshare.domain.party.dto.ParticipatedPartyDto;
import com.flab.weshare.domain.party.dto.ParticipatingPartySummary;
import com.flab.weshare.domain.party.dto.PartyCapsuleInfo;
import com.flab.weshare.domain.party.dto.PartyCreationRequest;
import com.flab.weshare.domain.party.dto.PartyInfo;
import com.flab.weshare.domain.party.dto.PartyJoinRequest;
import com.flab.weshare.domain.party.dto.SignContractResponse;
import com.flab.weshare.exception.ErrorCode;
import com.flab.weshare.exception.exceptions.CommonClientException;
import com.flab.weshare.exception.exceptions.CommonNotFoundException;
import com.flab.weshare.exception.exceptions.UnsatisfiedAuthorityException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartyService {
	private final PartyRepository partyRepository;
	private final PartyCapsuleRepository partyCapsuleRepository;
	private final PartyJoinRepository partyJoinRepository;
	private final OttRepository ottRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public Long generateParty(final PartyCreationRequest partyCreationRequest, final Long requestPartyLeaderId) {
		Ott requestOtt = ottRepository.findById(partyCreationRequest.ottId())
			.orElseThrow(() -> new CommonClientException(ErrorCode.makeSpecificResourceNotFoundErrorCode("ott")));

		validateCapacity(requestOtt, partyCreationRequest.capacity());

		User requestPartyLeader = userRepository.getReferenceById(requestPartyLeaderId);
		String encodedPassword = passwordEncoder.encode(partyCreationRequest.ottAccountPassword());
		Party generatedParty = saveParty(partyCreationRequest, requestOtt, requestPartyLeader, encodedPassword);

		savePartyCapsules(partyCreationRequest.capacity(), generatedParty);
		return generatedParty.getId();
	}

	private void savePartyCapsules(int partyCreationRequest, Party generatedParty) {
		List<PartyCapsule> generatedPartyCapsules = generateEmptyPartyCapsules(partyCreationRequest,
			generatedParty);
		partyCapsuleRepository.saveAll(generatedPartyCapsules);
	}

	private Party saveParty(PartyCreationRequest partyCreationRequest, Ott requestOtt,
		User requestPartyLeader,
		String encodedPassword) {
		Party generatedParty = Party.builder()
			.ott(requestOtt)
			.leader(requestPartyLeader)
			.capacity(partyCreationRequest.capacity())
			.ottAccountId(partyCreationRequest.ottAccountId())
			.ottAccountPassword(encodedPassword)
			.build();

		partyRepository.save(generatedParty);
		return generatedParty;
	}

	private List<PartyCapsule> generateEmptyPartyCapsules(int capacity, Party party) {
		List<PartyCapsule> generatedPartyCapsules = new ArrayList<>();
		for (int i = 0; i < capacity; i++) {
			generatedPartyCapsules.add(PartyCapsule.makeEmptyCapsule(party));
		}
		return generatedPartyCapsules;
	}

	private void validateCapacity(final Ott requestOtt, final int capacity) {
		if (!requestOtt.isValidCapacity(capacity)) {
			throw new CommonClientException(ErrorCode.INVALID_CAPACITY);
		}
	}

	@Transactional
	public void updatePartyDetails(final ModifyPartyRequest modifyPartyRequest, final Long partyId) {
		Party party = partyRepository.findFetchByPartyId(partyId)
			.orElseThrow(() -> new CommonClientException(ErrorCode.makeSpecificResourceNotFoundErrorCode("party")));

		validateCapacity(party.getOtt(), modifyPartyRequest.capacity());
		validateChangeableCapacity(party, modifyPartyRequest);

		String encodedPassword = passwordEncoder.encode(modifyPartyRequest.password());

		party.changeCapacity(modifyPartyRequest.capacity());
		party.changePassword(encodedPassword);

		synchronizeCapacity(party, modifyPartyRequest.capacity());
	}

	private void synchronizeCapacity(final Party party, final int newCapacity) {
		party.deleteEmptyCapsules(newCapacity);
		if (party.getCapsulesSize() < newCapacity) {
			savePartyCapsules(newCapacity - party.getCapsulesSize(), party);
		}
	}

	private void validateChangeableCapacity(final Party party, final ModifyPartyRequest modifyPartyRequest) {
		if (party.countOccupiedPartyCapsule() > modifyPartyRequest.capacity()) {
			throw new CommonClientException(ErrorCode.INSUFFICIENT_CAPACITY);
		}
	}

	@Transactional
	public Long generatePartyJoin(final PartyJoinRequest PartyJoinRequest, final Long userId) {
		User partyParticipant = userRepository.getReferenceById(userId);
		Ott selectedOtt = ottRepository.getReferenceById(PartyJoinRequest.ottId());

		PartyJoin partyJoin = PartyJoin.generateWaitingPartyJoin(partyParticipant, selectedOtt);
		partyJoinRepository.save(partyJoin);

		return partyJoin.getId();
	}

	@Transactional
	public void joinParty(final PartyJoin partyJoin, final PartyCapsule partyCapsule) {
		PartyJoin partyJoinPersist = partyJoinRepository.findByIdForUpdate(partyJoin.getId())
			.orElseThrow(
				() -> new IllegalArgumentException("partyJoin 엔티티가 존재하지 않음. partyJoinId = " + partyJoin.getId()));

		PartyCapsule partyCapsulePersist = partyCapsuleRepository.findByIdForUpdate(partyCapsule.getId())
			.orElseThrow(
				() -> new IllegalArgumentException("partyCapsule 엔티티가 존재하지 않음. partyCapsuleId = " + partyJoin.getId()));

		verifyWaitingPartyJoin(partyJoinPersist);
		verifyEmptyPartyCapsule(partyCapsulePersist);

		partyCapsulePersist.occupy(partyJoin.getUser());
		partyJoinPersist.changeStatusPayWaiting();
	}

	private void verifyEmptyPartyCapsule(PartyCapsule partyCapsule) {
		if (!partyCapsule.isEmptyCapsule()) {
			throw new IllegalArgumentException("파티 캡슐의 상태가 빈 상태가 아닙니다.");
		}
	}

	private void verifyWaitingPartyJoin(PartyJoin partyJoin) {
		if (!partyJoin.isWaitingPartyJoin()) {
			throw new IllegalArgumentException("파티 조인의 상태가 대기 상태가 아닙니다.");
		}
	}

	@Transactional(readOnly = true)
	public ParticipatedPartyDto findAllParticipatedParties(final Long userId) {
		log.info("userId {}", userId);
		User referenceById = userRepository.getReferenceById(userId);
		List<LeadingPartySummary> leadingPartySummaries = partyRepository.findByUserIdWithOtt(referenceById)
			.stream()
			.map(LeadingPartySummary::of)
			.toList();

		List<ParticipatingPartySummary> participatingPartySummaries = partyCapsuleRepository.findAllPartyCapsulesUserId(
				PartyCapsuleStatus.OCCUPIED, referenceById)
			.stream().map(ParticipatingPartySummary::of)
			.toList();

		return new ParticipatedPartyDto(leadingPartySummaries, participatingPartySummaries);
	}

	@Transactional(readOnly = true)
	public PartyInfo getPartyInfo(Long partyId, Long userId) {
		Party party = partyRepository.findFetchByPartyId(partyId)
			.orElseThrow(() -> new CommonNotFoundException(ErrorCode.makeSpecificResourceNotFoundErrorCode("party ")));
		checkAuthority(userId, party.getLeader().getId());
		return PartyInfo.of(party);
	}

	@Transactional(readOnly = true)
	public PartyCapsuleInfo getPartyCapsuleInfo(Long partyCapsuleId, Long userId) {
		PartyCapsule partyCapsule = partyCapsuleRepository.findPartyCapsuleById(partyCapsuleId)
			.orElseThrow(
				() -> new CommonNotFoundException(ErrorCode.makeSpecificResourceNotFoundErrorCode("partyCapsule ")));
		checkAuthority(userId, partyCapsule.getPartyMember().getId());
		return PartyCapsuleInfo.of(partyCapsule);
	}

	private void checkAuthority(Long userId, Long targetId) {
		if (!userId.equals(targetId)) {
			throw new UnsatisfiedAuthorityException(ErrorCode.INSUFFICIENT_AUTHORITY);
		}
	}

	public ContractRenewalResponse formContractRenewalResponse(final Long partyCapsuleId) {
		PartyCapsule partyCapsule = partyCapsuleRepository.findByIdForFetchAll(partyCapsuleId).orElseThrow(
			() -> new IllegalArgumentException("partyCapsule 엔티티가 존재하지 않음. partyCapsuleId = " + partyCapsuleId)
		);

		return new ContractRenewalResponse(partyCapsule.getPartyMember().getEmail(), partyCapsule.getExpirationDate(),
			partyCapsule.getParty().getOtt().getName());
	}

	@Transactional(readOnly = true)
	public SignContractResponse formSignContractResponse(final Long partyCapsuleId) {
		PartyCapsule partyCapsule = partyCapsuleRepository.findByIdForFetchAll(partyCapsuleId).orElseThrow(
			() -> new IllegalArgumentException("partyCapsule 엔티티가 존재하지 않음. partyCapsuleId = " + partyCapsuleId)
		);

		return new SignContractResponse(partyCapsule.getPartyMember().getEmail(), partyCapsule.getExpirationDate(),
			partyCapsule.getParty().getOtt().getName(), partyCapsule.getParty().getOttAccountId(),
			partyCapsule.getParty().getOttAccountPassword());
	}
}
