package com.flab.weshare.domain.pay.service;

import org.springframework.stereotype.Service;

import com.flab.weshare.domain.pay.dto.CardEnrollRequest;
import com.flab.weshare.domain.pay.entity.Card;
import com.flab.weshare.domain.pay.entity.Payment;
import com.flab.weshare.domain.pay.repository.CardRepository;
import com.flab.weshare.domain.paymentBatch.PayResult;
import com.flab.weshare.domain.user.repository.UserRepository;
import com.flab.weshare.utils.AesBytesEncryptUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardService {
	private final CardRepository cardRepository;
	private final UserRepository userRepository;
	private final PaymentProcessor paymentProcessor;
	private final AesBytesEncryptUtil aesBytesEncryptUtil;

	@Transactional
	public Long enrollCard(final CardEnrollRequest cardEnrollRequest, final Long userId) {
		String billingKey = paymentProcessor.requestBillingKey(cardEnrollRequest, userId);
		String encryptCardNumber = aesBytesEncryptUtil.encrypt(cardEnrollRequest.cardNumber());
		Card newCard = Card.buildNewCard(userRepository.getReferenceById(userId), billingKey, encryptCardNumber);

		return cardRepository.save(newCard).getId();
	}

	public PayResult payRequest(final Payment payment) {
		Card card = payment.getCard();
		String decryptedBillingKey = aesBytesEncryptUtil.decrypt(card.getBillingKey());
		return paymentProcessor.requestPayment(decryptedBillingKey, payment.getAmount().getIntegerAmount(), payment);
	}
}
