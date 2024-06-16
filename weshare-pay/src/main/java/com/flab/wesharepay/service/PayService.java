package com.flab.wesharepay.service;

import org.springframework.stereotype.Service;

import com.flab.core.entity.Card;
import com.flab.core.entity.PayResult;
import com.flab.core.entity.Payment;
import com.flab.core.infra.CardRepository;
import com.flab.core.infra.UserRepository;
import com.flab.wesharepay.dto.CardEnrollRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayService {
	private final CardRepository cardRepository;
	private final UserRepository userRepository;
	private final PaymentProcessor paymentProcessor;

	@Transactional
	public Long enrollCard(final String cardEnrollRequest, final Long userId) {
		String billingKey = paymentProcessor.requestBillingKey(cardEnrollRequest, userId);
		Card newCard = Card.buildNewCard(userRepository.getReferenceById(userId), billingKey, );

		return cardRepository.save(newCard).getId();
	}

	public PayResult payRequest(final Payment payment) throws InterruptedException {
		Card card = payment.getCard();
		Thread.sleep(10000);
		//card.getBillingKey();
		//return paymentProcessor.requestPayment(card.getBillingKey(), payment.getAmount().getIntegerAmount(), payment);//
		return PayResult.ofSuccessfulPayResult(payment, "{\"name\":\"sonoo\",\"salary\":600000.0,\"age\":27}");
	}
}
