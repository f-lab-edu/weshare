package com.flab.weshare.domain.pay.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.flab.weshare.domain.pay.dto.CardEnrollRequest;
import com.flab.weshare.exception.ErrorCode;
import com.flab.weshare.exception.exceptions.CommonClientException;

import kr.co.bootpay.Bootpay;
import kr.co.bootpay.model.request.Subscribe;
import kr.co.bootpay.model.request.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BootPayPaymentProcessor implements PaymentProcessor {
	private static final String ORDER_NAME = "weshare ott서비스";
	private static final String PG = "나이스페이먼츠";

	private final String restApplicationId;
	private final String privateKey;

	public BootPayPaymentProcessor(@Value("${pay.rest_application_id}") String restApplicationId
		, @Value("${pay.private_key}") String privateKey) {
		this.restApplicationId = restApplicationId;
		this.privateKey = privateKey;
	}

	@Override
	public String requestBillingKey(CardEnrollRequest cardEnrollRequest, Long userId) {
		Bootpay bootpay = new Bootpay(restApplicationId, privateKey);
		acquireAccessToken(bootpay);

		Subscribe subscribe = createNewSubScribe(cardEnrollRequest, userId);
		try {
			HashMap<String, Object> res = bootpay.getBillingKey(subscribe);
			if (res.get("error_code") == null) {
				return res.get("billing_key").toString();
			} else {
				log.info("빌링 키 발급 실패 {}", res);
				throw new CommonClientException(ErrorCode.FAIL_CARD_ENROLLMENT);
			}
		} catch (Exception e) {
			log.error("빌링키 발급 중 문제 발생", e);
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * 구현 예정
	 */
	@Override
	public String requestPayment() {
		return null;
	}

	private Subscribe createNewSubScribe(CardEnrollRequest cardEnrollRequest, Long userId) {
		Subscribe subscribe = new Subscribe();
		subscribe.orderName = ORDER_NAME;
		subscribe.subscriptionId = "" + (System.currentTimeMillis() / 1000);
		subscribe.pg = PG;
		subscribe.cardNo = cardEnrollRequest.cardNumber();
		subscribe.cardPw = cardEnrollRequest.cardPw();
		subscribe.cardExpireYear = cardEnrollRequest.cardExpireYear();
		subscribe.cardExpireMonth = cardEnrollRequest.cardExpireMonth();
		subscribe.cardIdentityNo = cardEnrollRequest.birthDate();
		subscribe.user = new User();
		subscribe.user.id = String.valueOf(userId);
		return subscribe;
	}

	private void acquireAccessToken(Bootpay bootpay) {
		try {
			bootpay.getAccessToken();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
