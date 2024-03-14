package com.flab.weshare.domain.party.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flab.weshare.domain.party.dto.ModifyPartyRequest;
import com.flab.weshare.domain.party.dto.PartyCreationRequest;
import com.flab.weshare.domain.party.entity.Ott;
import com.flab.weshare.domain.party.entity.Party;
import com.flab.weshare.domain.party.repository.OttRepository;
import com.flab.weshare.domain.party.repository.PartyRepository;
import com.flab.weshare.domain.user.entity.User;
import com.flab.weshare.domain.user.repository.UserRepository;
import com.flab.weshare.exception.ErrorCode;
import com.flab.weshare.exception.exceptions.CommonClientException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PartyService {
	private final PartyRepository partyRepository;
	private final OttRepository ottRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public Long generateParty(final PartyCreationRequest partyCreationRequest, final Long requestPartyLeaderId) {
		Ott requestOtt = ottRepository.findById(partyCreationRequest.ottId())
			.orElseThrow(() -> new CommonClientException(ErrorCode.makeSpecificResourceNotFoundErrorCode("ott")));
		User requestPartyLeader = userRepository.getReferenceById(requestPartyLeaderId);
		String encodedPassword = passwordEncoder.encode(partyCreationRequest.ottAccountPassword());

		validateCapacity(requestOtt, partyCreationRequest.capacity());

		Party generateParty = Party.builder()
			.ott(requestOtt)
			.leader(requestPartyLeader)
			.capacity(partyCreationRequest.capacity())
			.ottAccountId(partyCreationRequest.ottAccountId())
			.ottAccountPassword(encodedPassword)
			.build();

		partyRepository.save(generateParty);

		return generateParty.getId();
	}

	private void validateCapacity(final Ott requestOtt, final int capacity) {
		if (!requestOtt.isValidCapacity(capacity)) {
			throw new CommonClientException(ErrorCode.INVALID_CAPACITY);
		}
	}

	@Transactional
	public void updatePartyDetails(final Long partyId, final ModifyPartyRequest modifyPartyRequest) {
		Party party = partyRepository.findFetchByPartyId(partyId)
			.orElseThrow(() -> new CommonClientException(ErrorCode.makeSpecificResourceNotFoundErrorCode("party")));
		String encodedPassword = passwordEncoder.encode(modifyPartyRequest.password());

		validateCapacity(party.getOtt(), modifyPartyRequest.capacity());
		validateChangeableCapacity(party, modifyPartyRequest);

		party.changeCapacity(modifyPartyRequest.capacity());
		party.changePassword(encodedPassword);
	}

	private void validateChangeableCapacity(final Party party, final ModifyPartyRequest modifyPartyRequest) {
		if (!party.isChangeableCapacity(modifyPartyRequest.capacity())) {

			throw new CommonClientException(ErrorCode.INSUFFICIENT_CAPACITY);
		}
	}
}
