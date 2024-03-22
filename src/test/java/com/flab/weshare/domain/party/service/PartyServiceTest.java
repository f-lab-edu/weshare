package com.flab.weshare.domain.party.service;

import static com.flab.weshare.domain.utils.TestUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.flab.weshare.domain.party.dto.ModifyPartyRequest;
import com.flab.weshare.domain.party.dto.PartyCreationRequest;
import com.flab.weshare.domain.party.entity.Party;
import com.flab.weshare.domain.party.entity.PartyCapsule;
import com.flab.weshare.domain.party.entity.PartyCapsuleStatus;
import com.flab.weshare.domain.party.repository.OttRepository;
import com.flab.weshare.domain.party.repository.PartyCapsuleRepository;
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
	PartyCapsuleRepository partyCapsuleRepository;

	@Mock
	OttRepository ottRepository;

	@Mock
	UserRepository userRepository;

	@Mock
	PasswordEncoder passwordEncoder;

	@Mock
	private Party mockParty;

	@DisplayName("유효한 파티 생성 요청일 시 파티와 빈 파티 캡슐을 정원 만큼 생성하며 데이터베이스 저장한다.")
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
		then(partyCapsuleRepository).should(times(1))
			.saveAll(ArgumentMatchers.argThat(
				argument -> argument instanceof List<PartyCapsule>
					&& ((List<PartyCapsule>)argument).size() == PARTY_MAXIMUM_CAPACITY //파티의 정원 만큼 파티캡슐을 생성하는가
					&& ((List<PartyCapsule>)argument).stream() //파티 캡슐이 모두 빈 상태로 생성되는가
					.allMatch(partyCapsule
						-> partyCapsule.getPartyCapsuleStatus().equals(PartyCapsuleStatus.EMPTY))
			));
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

		assertThatThrownBy(() -> partyService.generateParty(partyCreationRequest, 1L))
			.isInstanceOf(CommonClientException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.INVALID_CAPACITY);
	}

	@DisplayName("파티 수정 성공 - 1. 정원 감소")
	@Test
	void update_party_success_decrease_capacity() {
		int newCapacity = 2;
		int partyCapsuleSize = 3;
		int occupiedCapsuleCount = 2;

		ModifyPartyRequest modifyPartyRequest = new ModifyPartyRequest(newCapacity, PASSWORD);

		given(partyRepository.findFetchByPartyId(anyLong())).willReturn(Optional.of(mockParty));
		given(mockParty.getOtt()).willReturn(savedOtt); //현재 ott 최대 정원 : 4
		given(mockParty.countOccupiedPartyCapsule()).willReturn(occupiedCapsuleCount); //현재 파티에 참여 중인 인원 : 2
		given(mockParty.getCapsulesSize()).willReturn(partyCapsuleSize); // 전체 캡슐수 : 3 빈 캡슐 : 1 상황 가정

		partyService.updatePartyDetails(modifyPartyRequest, 1L);

		then(mockParty).should(times(1)).deleteEmptyCapsules(newCapacity);
		then(mockParty).should(times(1)).changePassword(passwordEncoder.encode(modifyPartyRequest.password()));
		then(mockParty).should(times(1)).changeCapacity(modifyPartyRequest.capacity());
		then(partyCapsuleRepository).should(never()).saveAll(anyCollection());
	}

	@DisplayName("파티 수정 성공 - 2. 정원 증가")
	@Test
	void update_party_success_increase_capacity() {
		int newCapacity = 4;
		int partyCapsuleSize = 3;
		int occupiedCapsuleCount = 2;

		ModifyPartyRequest modifyPartyRequest = new ModifyPartyRequest(newCapacity, PASSWORD);

		given(partyRepository.findFetchByPartyId(anyLong())).willReturn(Optional.of(mockParty));
		given(mockParty.getOtt()).willReturn(savedOtt); //현 재 ott 최대 정원 : 4
		given(mockParty.countOccupiedPartyCapsule()).willReturn(occupiedCapsuleCount); //현재 파티에 참여 중인 인원 : 2
		given(mockParty.getCapsulesSize()).willReturn(partyCapsuleSize); // 전체 캡슐수 : 3 빈 캡슐 : 1 상황 가정

		partyService.updatePartyDetails(modifyPartyRequest, 1L);

		then(mockParty).should(times(1)).changePassword(passwordEncoder.encode(modifyPartyRequest.password()));
		then(mockParty).should(times(1)).changeCapacity(modifyPartyRequest.capacity());
		then(partyCapsuleRepository).should(times(1))
			.saveAll(ArgumentMatchers.argThat(
				argument -> argument instanceof List<PartyCapsule>
					&& ((List<PartyCapsule>)argument).size() == newCapacity - partyCapsuleSize
					&& ((List<PartyCapsule>)argument).stream()
					.allMatch(partyCapsule
						-> partyCapsule.getPartyCapsuleStatus().equals(PartyCapsuleStatus.EMPTY))
			));
	}

	@DisplayName("현재 파티 점유인원 보다 정원을 적게 변경하는 요청시 예외를 반환")
	@Test
	void update_party_fail() {
		int newCapacity = 2;
		int partyCapsuleSize = 3;
		int occupiedCapsuleCount = 3;

		ModifyPartyRequest modifyPartyRequest = new ModifyPartyRequest(newCapacity, PASSWORD);

		given(partyRepository.findFetchByPartyId(anyLong())).willReturn(Optional.of(mockParty));
		given(mockParty.getOtt()).willReturn(savedOtt);
		given(mockParty.countOccupiedPartyCapsule()).willReturn(occupiedCapsuleCount);

		assertThatThrownBy(() -> partyService.updatePartyDetails(modifyPartyRequest, 1L))
			.isInstanceOf(CommonClientException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.INSUFFICIENT_CAPACITY);
	}
}
