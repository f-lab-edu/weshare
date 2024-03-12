package com.flab.weshare.domain.party.service;

import static com.flab.weshare.domain.utils.TestUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.flab.weshare.domain.party.dto.ModifyPartyRequest;
import com.flab.weshare.domain.party.dto.PartyCreationRequest;
import com.flab.weshare.domain.party.entity.Party;
import com.flab.weshare.domain.party.repository.OttRepository;
import com.flab.weshare.domain.party.repository.PartyRepository;
import com.flab.weshare.domain.user.repository.UserRepository;
import com.flab.weshare.exception.ErrorCode;
import com.flab.weshare.exception.exceptions.CommonClientException;

@ExtendWith(MockitoExtension.class)
class PartyServiceTest {
	@InjectMocks
	PartyService partyService;

	@Mock
	PartyRepository partyRepository;

	@Mock
	OttRepository ottRepository;

	@Mock
	UserRepository userRepository;

	@Mock
	PasswordEncoder passwordEncoder;

	@Mock
	private Party mockParty;

	@DisplayName("유효한 파티 생성 요청일 시 데이터베이스에 저장.")
	@Test
	void generate_party_sucesss() {
		PartyCreationRequest partyCreationRequest = new PartyCreationRequest(
			1L, OTT_ACCOUNT_ID, OTT_PASSWORD, PARTY_MAXIMUM_CAPACITY);

		given(ottRepository.findById(anyLong())).willReturn(Optional.of(savedOtt));
		given(userRepository.getReferenceById(anyLong())).willReturn(savedUser);

		ReflectionTestUtils.setField(savedOtt, "id", 1L);
		ReflectionTestUtils.setField(savedUser, "id", 1L);

		partyService.generateParty(partyCreationRequest, savedUser.getId());

		then(partyRepository).should(times(1)).save(any(Party.class));
	}

	@DisplayName("생성하려는 파티의 ott를 찾을 수 없는경우 예외를 반환")
	@Test
	void generate_party_fail_not_found_ott() {
		PartyCreationRequest partyCreationRequest = new PartyCreationRequest(
			1L, OTT_ACCOUNT_ID, OTT_PASSWORD, PARTY_MAXIMUM_CAPACITY);

		given(ottRepository.findById(anyLong())).willReturn(Optional.empty());

		assertThatThrownBy(() -> partyService.generateParty(partyCreationRequest, 1L))
			.isInstanceOf(CommonClientException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.makeSpecificResourceNotFoundErrorCode("ott"));
	}

	@DisplayName("파티의 정원이 올바르지않을 경우 예외를 반환")
	@Test
	void generate_party_fail_invalid_capacity() {
		PartyCreationRequest partyCreationRequest = new PartyCreationRequest(
			1L, OTT_ACCOUNT_ID, OTT_PASSWORD, MAXIMUM_CAPACITY + 1);

		given(ottRepository.findById(anyLong())).willReturn(Optional.of(savedOtt));
		given(userRepository.getReferenceById(anyLong())).willReturn(savedUser);

		ReflectionTestUtils.setField(savedOtt, "id", 1L);
		ReflectionTestUtils.setField(savedUser, "id", 1L);

		assertThatThrownBy(() -> partyService.generateParty(partyCreationRequest, 1L))
			.isInstanceOf(CommonClientException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.INVALID_CAPACITY);
	}

	@DisplayName("유효한 파티 수정요청일 경우 파티의 상태를 업데이트한다.")
	@Test
	void update_party_success() {
		ModifyPartyRequest modifyPartyRequest = new ModifyPartyRequest(3, PASSWORD);

		given(partyRepository.findFetchByPartyId(anyLong())).willReturn(Optional.of(mockParty));
		given(mockParty.getOtt()).willReturn(savedOtt);
		given(mockParty.isChangeableCapacity(anyInt())).willReturn(true);

		partyService.updatePartyDetails(1L, modifyPartyRequest);
		then(mockParty).should(times(1)).changePassword(passwordEncoder.encode(modifyPartyRequest.password()));
		then(mockParty).should(times(1)).changeCapacity(modifyPartyRequest.capacity());
	}

	@DisplayName("현재 파티 인원보다 적게 변경하는 요청시 예외를 반환")
	@Test
	void update_party_fail() {
		ModifyPartyRequest modifyPartyRequest = new ModifyPartyRequest(3, PASSWORD);

		given(partyRepository.findFetchByPartyId(anyLong())).willReturn(Optional.of(mockParty));
		given(mockParty.getOtt()).willReturn(savedOtt);
		given(mockParty.isChangeableCapacity(anyInt())).willReturn(false);

		assertThatThrownBy(() -> partyService.updatePartyDetails(1L, modifyPartyRequest))
			.isInstanceOf(CommonClientException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.INSUFFICIENT_CAPACITY);
	}
}
