package com.flab.weshare.domain.partyjoin;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Optional;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.flab.core.entity.BillingKey;
import com.flab.core.entity.BillingKeyStatus;
import com.flab.core.entity.Money;
import com.flab.core.entity.Ott;
import com.flab.core.entity.Party;
import com.flab.core.entity.PartyCapsule;
import com.flab.core.entity.PartySign;
import com.flab.core.entity.User;
import com.flab.core.infra.BillingKeyRepository;
import com.flab.core.infra.OttRepository;
import com.flab.core.infra.PartyCapsuleRepository;
import com.flab.core.infra.PartySignRepository;
import com.flab.core.infra.PayResRepository;
import com.flab.core.infra.UserRepository;
import com.flab.mail.mail.service.MailPublisher;
import com.flab.weshare.domain.base.BaseServiceTest;
import com.flab.weshare.domain.partyMatch.service.EmptyCapsulePublisher;
import com.flab.weshare.domain.partyMatch.service.PartyJoinMessage;
import com.flab.weshare.domain.partyMatch.service.PartyMatcher;
import com.flab.weshare.domain.partyMatch.service.exception.BillingKeyNotFoundException;
import com.flab.weshare.domain.partyMatch.service.exception.EmptyPartyCapsuleNotFoundException;
import com.flab.weshare.domain.partyMatch.service.exception.PayFailedException;
import com.flab.wesharepay.dto.TossPaymentRequest;
import com.flab.wesharepay.exception.PaymentException;
import com.flab.wesharepay.service.TossPaymentProcessor;

public class PartyMatcherTest extends BaseServiceTest {
	@Mock
	private BillingKeyRepository billingKeyRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private TossPaymentProcessor tossPaymentProcessor;
	@Mock
	private PartySignRepository partySignRepository;
	@Mock
	private EmptyCapsulePublisher emptyCapsulePublisher;
	@Mock
	private PartyCapsuleRepository partyCapsuleRepository;
	@Mock
	private PayResRepository payResRepository;

	@Mock
	private OttRepository ottRepository;
	@Mock
	private MailPublisher mailPublisher;
	@InjectMocks
	private PartyMatcher partyMatcher;
	@Mock
	private Ott ott;
	@Mock
	private PartyCapsule partyCapsule;
	@Mock
	private BillingKey billingKey;
	@Mock
	private User user;
	@Mock
	private PartySign partySign;
	@Mock
	private Party party;

	@Test
	@DisplayName("결제 성공후 파티 매치까지 모두 완료하면 Party Res를 반환한다.")
	void partyMatchReturnsPartySignOnSuccess() throws IOException {
		Long ottId = 1L, userId = 2L, capsuleId = 3L;
		when(ottRepository.findById(ottId)).thenReturn(Optional.ofNullable(ott));
		when(partyCapsuleRepository.findById(capsuleId)).thenReturn(Optional.ofNullable(partyCapsule));
		when(ott.getPerDayPrice()).thenReturn(new Money(100));
		when(billingKey.getUser()).thenReturn(user);
		when(billingKey.getBillingKey()).thenReturn("billing key");
		when(user.getClientId()).thenReturn("uuid");
		when(partyCapsule.getParty()).thenReturn(party);
		when(party.getOttAccountId()).thenReturn("accontId");
		when(party.getOttAccountPassword()).thenReturn("password");
		when(userRepository.getReferenceById(userId)).thenReturn(user);
		when(billingKeyRepository.findByUserAndBillingKeyStatus(user, BillingKeyStatus.ISSUED)).thenReturn(billingKey);
		when(tossPaymentProcessor.requestPayment(anyString(), any(TossPaymentRequest.class))).thenReturn(
			new JSONObject() {{
				put("approvedAt", OffsetDateTime.now().toString());
				put("status", "SUCCESS");
			}});
		when(partySignRepository.save(any())).thenReturn(partySign);

		PartySign result = partyMatcher.partyMatch(ottId, userId, capsuleId);

		assertThat(result).isNotNull();
	}

	@Test
	@DisplayName("결제 실패시 예외를 던지며, 파티매칭은 되지않고 파티캡슐은 롤백된다.")
	void partyMatchThrowsPayFailedExceptionOnPaymentFail() throws IOException {
		Long ottId = 1L, userId = 2L, capsuleId = 3L;
		when(ottRepository.findById(ottId)).thenReturn(Optional.ofNullable(ott));
		when(partyCapsuleRepository.findById(capsuleId)).thenReturn(Optional.ofNullable(partyCapsule));
		when(userRepository.getReferenceById(userId)).thenReturn(user);
		when(billingKey.getUser()).thenReturn(user);
		when(billingKey.getBillingKey()).thenReturn("billing key");
		when(user.getClientId()).thenReturn("uuid");
		when(ott.getPerDayPrice()).thenReturn(new Money(100));
		when(billingKeyRepository.findByUserAndBillingKeyStatus(user, BillingKeyStatus.ISSUED)).thenReturn(billingKey);
		when(tossPaymentProcessor.requestPayment(anyString(), any(TossPaymentRequest.class))).thenThrow(
			new PaymentException("error"));

		assertThatThrownBy(() -> partyMatcher.partyMatch(ottId, userId, capsuleId))
			.isInstanceOf(PayFailedException.class);
		verifyMessageRollBacked(ottId, capsuleId);
	}

	private void verifyMessageRollBacked(Long ottId, Long capsuleId) {
		ArgumentCaptor<PartyJoinMessage> arg = ArgumentCaptor.forClass(PartyJoinMessage.class);
		verify(emptyCapsulePublisher, times(1)).publishPartySlotAvailable(arg.capture());
		assertThat(arg.getValue().ottId()).isEqualTo(String.valueOf(ottId));
		assertThat(arg.getValue().partySlotId()).isEqualTo(String.valueOf(capsuleId));
	}

	@Test
	@DisplayName("partyMatch throws EmptyPartyCapsuleNotFoundException when capsule not found")
	void partyMatchThrowsEmptyPartyCapsuleNotFoundException() throws IOException {
		Long ottId = 1L, userId = 2L, capsuleId = 3L;
		when(ottRepository.findById(ottId)).thenReturn(Optional.ofNullable(ott));
		when(partyCapsuleRepository.findById(capsuleId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> partyMatcher.partyMatch(ottId, userId, capsuleId))
			.isInstanceOf(EmptyPartyCapsuleNotFoundException.class);

		verify(tossPaymentProcessor, never()).requestPayment(any(), any());
		verify(emptyCapsulePublisher, never()).publishPartySlotAvailable(any());
		verify(mailPublisher, never()).publishSuccessPartyJoinMessage(any(), any(), any(), any(), any(), any(), any());
	}

	@Test
	@DisplayName("결제를 위한 빌링키 찾지 못할시 파티캡슐은 롤백되며 예외를 반환.")
	void partyMatchThrowsBillingKeyNotFoundException() {
		Long ottId = 1L, userId = 2L, capsuleId = 3L;
		when(ottRepository.findById(ottId)).thenReturn(Optional.ofNullable(ott));
		when(partyCapsuleRepository.findById(capsuleId)).thenReturn(Optional.ofNullable(partyCapsule));
		when(userRepository.getReferenceById(userId)).thenReturn(user);
		when(billingKeyRepository.findByUserAndBillingKeyStatus(user, BillingKeyStatus.ISSUED)).thenThrow(
			new RuntimeException());

		assertThatThrownBy(() -> partyMatcher.partyMatch(ottId, userId, capsuleId))
			.isInstanceOf(BillingKeyNotFoundException.class);
		verifyMessageRollBacked(ottId, capsuleId);
	}
}
