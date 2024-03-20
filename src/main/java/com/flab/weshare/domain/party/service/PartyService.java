package com.flab.weshare.domain.party.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flab.weshare.domain.party.dto.ModifyPartyRequest;
import com.flab.weshare.domain.party.dto.PartyCreationRequest;
import com.flab.weshare.domain.party.dto.PartyJoinRequest;
import com.flab.weshare.domain.party.entity.Ott;
import com.flab.weshare.domain.party.entity.Party;
import com.flab.weshare.domain.party.entity.PartyCapsule;
import com.flab.weshare.domain.party.entity.PartyJoin;
import com.flab.weshare.domain.party.repository.OttRepository;
import com.flab.weshare.domain.party.repository.PartyCapsuleRepository;
import com.flab.weshare.domain.party.repository.PartyJoinRepository;
import com.flab.weshare.domain.party.repository.PartyRepository;
import com.flab.weshare.domain.user.entity.User;
import com.flab.weshare.domain.user.repository.UserRepository;
import com.flab.weshare.exception.ErrorCode;
import com.flab.weshare.exception.exceptions.CommonClientException;

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
		Party generatedParty = Party.builder()
			.ott(requestOtt)
			.leader(requestPartyLeader)
			.capacity(partyCreationRequest.capacity())
			.ottAccountId(partyCreationRequest.ottAccountId())
			.ottAccountPassword(encodedPassword)
			.build();

		partyRepository.save(generatedParty);

		List<PartyCapsule> generatedPartyCapsules = generateEmptyPartyCapsules(partyCreationRequest.capacity(),
			generatedParty);
		partyCapsuleRepository.saveAll(generatedPartyCapsules);
		return generatedParty.getId();
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

	/**
	 * synchrnoizeCapacity()
	 * 1) 만약 현재 occupied + empty > capcity
	 * empty PartyCapsule의 숫자를 capacity - occupied로 맞춤.
	 * empty PartyCapsule의 상태를 변경. empty->delete
	 * <p>
	 * 2) 만약 현재 occupied + empty == capcity
	 * 아무것도 하지않음
	 * <p>
	 * 3) 만약 현재 occupied + empty < capacity
	 * empty PartyCapsule의 숫자를 추가.
	 * 이게 문제임. delete시 ==> 차라리 delete를 일
	 */
	private void synchronizeCapacity(final Party party, final int newCapacity) {
		if (party.getCapsulesSize() > newCapacity) {
			party.deleteEmptyCapsules(party.getCapsulesSize()
				- newCapacity);  // empty : 1개 occu:2개 capa :3개 full -> capa:2개 -> size - capa delete
			return;
		}

		if (party.getCapsulesSize() < newCapacity) {
			List<PartyCapsule> partyCapsules = generateEmptyPartyCapsules(newCapacity - party.getCapsulesSize(), party);
			partyCapsuleRepository.saveAll(partyCapsules);
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

		PartyJoin partyJoin = PartyJoin.builder()
			.ott(selectedOtt)
			.user(partyParticipant)
			.build();

		partyJoinRepository.save(partyJoin);

		return partyJoin.getId();
	}
}
