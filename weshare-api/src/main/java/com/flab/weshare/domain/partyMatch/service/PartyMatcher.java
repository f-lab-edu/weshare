package com.flab.weshare.domain.partyMatch.service;

import static jakarta.transaction.Transactional.TxType.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.flab.core.entity.BillingKey;
import com.flab.core.entity.BillingKeyStatus;
import com.flab.core.entity.Money;
import com.flab.core.entity.Ott;
import com.flab.core.entity.PartyCapsule;
import com.flab.core.entity.PartySign;
import com.flab.core.entity.PayRes;
import com.flab.core.entity.User;
import com.flab.core.infra.BillingKeyRepository;
import com.flab.core.infra.OttRepository;
import com.flab.core.infra.PartyCapsuleRepository;
import com.flab.core.infra.PartySignRepository;
import com.flab.core.infra.PayResRepository;
import com.flab.core.infra.UserRepository;
import com.flab.core.utils.PayHelper;
import com.flab.mail.mail.service.MailPublisher;
import com.flab.weshare.domain.partyMatch.service.exception.BillingKeyNotFoundException;
import com.flab.weshare.domain.partyMatch.service.exception.EmptyPartyCapsuleNotFoundException;
import com.flab.weshare.domain.partyMatch.service.exception.PayFailedException;
import com.flab.wesharepay.dto.TossPaymentRequest;
import com.flab.wesharepay.exception.PaymentException;
import com.flab.wesharepay.service.TossPaymentProcessor;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class PartyMatcher {
	private final BillingKeyRepository billingKeyRepository;
	private final UserRepository userRepository;
	private final TossPaymentProcessor tossPaymentProcessor;
	private final PartySignRepository partySignRepository;
	private final EmptyCapsulePublisher emptyCapsulePublisher;
	private final PartyCapsuleRepository partyCapsuleRepository;
	private final OttRepository ottRepository;
	private final MailPublisher mailPublisher;
	private final ApplicationEventPublisher eventPublisher;
	private final PayResRepository payResRepository;

	@Transactional
	public PartySign partyMatch(final Long ottId, final Long userId, final Long partyCapsuleId) throws IOException {
		Ott ott = getOtt(ottId);
		PartyCapsule partyCapsule = getPartyCapsule(partyCapsuleId);
		BillingKey billingKey = getBillingKey(ottId, userId, partyCapsuleId);
		PayRes payRes = handlePayment(billingKey.getUser(), ott, billingKey);

		if (!payRes.isSuccess()) {
			rollBack(partyCapsuleId, ottId);
			log.error("payment failed : {} ", payRes.getErrorMessage());
			throw new PayFailedException("payment failed : " + payRes.getErrorMessage());
		}

		PartySign signed = join(partyCapsule, payRes, billingKey.getUser());
		sendMail(billingKey.getUser(), partyCapsule, ott);
		return signed;
	}

	@Transactional(value = REQUIRES_NEW, dontRollbackOn = Exception.class)
	public PayRes handlePayment(User user, Ott ott, BillingKey billingKey) throws IOException {
		TossPaymentRequest tossPaymentRequest = buildTossPaymentRequest(user, ott);
		PayRes payRes;
		try {
			JSONObject payResponse = tossPaymentProcessor.requestPayment(billingKey.getBillingKey(),
				tossPaymentRequest);
			payRes = PayRes.success(user, payResponse,
				OffsetDateTime.parse((String)payResponse.get("approvedAt")).toLocalDateTime());
		} catch (PaymentException e) {
			payRes = PayRes.fail(user, e.getErrorResponse());
		}
		payResRepository.save(payRes);
		return payRes;
	}

	private PartyCapsule getPartyCapsule(Long partyCapsuleId) {
		PartyCapsule partyCapsule;
		try {
			partyCapsule = getEmptyPartyCapsule(partyCapsuleId);
		} catch (Exception e) {
			log.error("failed to get empty partyCapsule id : {} ", partyCapsuleId, e);
			throw new EmptyPartyCapsuleNotFoundException("partyCapsule id : " + partyCapsuleId + "not found");
		}
		return partyCapsule;
	}

	private BillingKey getBillingKey(Long ottId, Long userId, Long partyCapsuleId) {
		BillingKey billingKey;
		try {
			billingKey = billingKeyRepository.findByUserAndBillingKeyStatus(
				userRepository.getReferenceById(userId), BillingKeyStatus.ISSUED);
		} catch (Exception e) {
			log.error("failed to get BillingKey : {} ", userId, e);
			rollBack(partyCapsuleId, ottId);
			throw new BillingKeyNotFoundException("빌링키 찾을 수 없음.");
		}
		return billingKey;
	}

	private Ott getOtt(Long ottId) {
		Ott ott = ottRepository.findById(ottId).get();
		return ott;
	}

	private PartyCapsule getEmptyPartyCapsule(long emptyCapsuleId) {
		PartyCapsule partyCapsule = partyCapsuleRepository.findById(emptyCapsuleId)
			.orElseThrow(() -> new IllegalArgumentException(
				"partyCapsule id : " + emptyCapsuleId + "not found from emptyCapsuleQueue"));
		return partyCapsule;
	}

	private void rollBack(long partyCapsuleId, long ottId) {
		log.info("roll back 됨 - 파티캡슐 id : {} , ottId : {}", partyCapsuleId, ottId);
		emptyCapsulePublisher.publishPartySlotAvailable(
			new PartyJoinMessage(String.valueOf(partyCapsuleId), String.valueOf(ottId)));
	}

	private PartySign join(PartyCapsule partyCapsule, PayRes payRes, User user) {
		PartySign partySign = PartySign.builder()
			.partyCapsule(partyCapsule)
			.payRes(payRes)
			.build();
		partySignRepository.save(partySign);
		partyCapsule.partyJoin(user, payRes.getPayApprovedAt(),
			PayHelper.getNextExpirationDate(payRes.getPayApprovedAt()));
		return partySign;
	}

	private void sendMail(User user, PartyCapsule partyCapsule, Ott ott) {
		mailPublisher.publishSuccessPartyJoinMessage(
			user.getEmail(), LocalDateTime.now(), user.getNickName(), LocalDateTime.now().plusMonths(1),
			partyCapsule.getParty().getOttAccountId(), partyCapsule.getParty().getOttAccountPassword(),
			ott.getName()
		);
	}

	private TossPaymentRequest buildTossPaymentRequest(User user, Ott ott) {
		LocalDateTime currTime = LocalDateTime.now();
		LocalDateTime nextExpirationDate = PayHelper.getNextExpirationDate(currTime);
		Money paymentAmount = PayHelper.calculateMoney(currTime, nextExpirationDate, ott.getPerDayPrice());

		return new TossPaymentRequest(
			user.getClientId(),
			paymentAmount.getIntegerAmount(),
			UUID.randomUUID().toString(),
			ott.getName()
		);
	}
}
