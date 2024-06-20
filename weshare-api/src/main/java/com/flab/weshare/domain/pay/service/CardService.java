package com.flab.weshare.domain.pay.service;

import org.springframework.stereotype.Service;

import com.flab.core.entity.Card;
import com.flab.core.infra.CardRepository;
import com.flab.core.infra.UserRepository;
import com.flab.weshare.domain.pay.dto.CardEnrollRequest;
import com.flab.weshare.utils.AesBytesEncryptUtil;
import com.flab.wesharepay.service.CardInfo;
import com.flab.wesharepay.service.PayServiceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardService {
	private final UserRepository userRepository;
	private final PayServiceImpl payService;
	private final AesBytesEncryptUtil aesBytesEncryptUtil;
	private final CardRepository cardRepository;

	@Transactional
	public Long enrollCard(final CardEnrollRequest cardEnrollRequest, final Long userId) {
		String billingKey = payService.enrollCard(generateCardInfo(cardEnrollRequest), userId);
		String encryptCardNumber = aesBytesEncryptUtil.encrypt(cardEnrollRequest.cardNumber());
		Card newCard = Card.buildNewCard(userRepository.getReferenceById(userId), billingKey, encryptCardNumber);
		return cardRepository.save(newCard).getId();
	}

	private CardInfo generateCardInfo(CardEnrollRequest cardEnrollRequest) {
		return new CardInfo(cardEnrollRequest.cardNumber()
			, cardEnrollRequest.cardPw()
			, cardEnrollRequest.cardExpireYear()
			, cardEnrollRequest.cardExpireMonth()
			, cardEnrollRequest.birthDate());
	}
}
