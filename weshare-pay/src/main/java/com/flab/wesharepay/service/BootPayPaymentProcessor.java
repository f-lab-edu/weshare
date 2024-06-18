package com.flab.wesharepay.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.flab.wesharepay.exception.CommonPayServiceException;
import com.flab.wesharepay.exception.InvalidRequestBillingKeyException;

import kr.co.bootpay.Bootpay;
import kr.co.bootpay.model.request.Subscribe;
import kr.co.bootpay.model.request.SubscribePayload;
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
	public String requestBillingKey(CardInfo cardInfo, Long userId) {
		Bootpay bootpay = bootPaySetUp();

		Subscribe subscribe = createNewSubScribe(cardInfo, userId);
		try {
			HashMap<String, Object> res = bootpay.getBillingKey(subscribe);
			if (res.get("error_code") == null) {
				return res.get("billing_key").toString();
			} else {
				throw new InvalidRequestBillingKeyException((String)res.get("message"));
			}
		} catch (Exception e) {
			log.error("빌링키 발급 중 문제 발생", e);
			throw new CommonPayServiceException(e.getMessage());
		}
	}

	@Override
	public Receipt requestPayment(String billingKey, Integer amount, Long orderId) {
		Bootpay bootpay = bootPaySetUp();
		SubscribePayload payload = createNewSubScribePayLoad(billingKey, amount, String.valueOf(orderId));
		try {
			HashMap<String, Object> res = bootpay.requestSubscribe(payload);
			if (res.get("error_code") == null) {
				return Receipt.successReceipt(res);
			} else {
				return Receipt.failReceipt(res);
			}
		} catch (Exception e) {
			throw new CommonPayServiceException(e.getMessage());
		}
	}

	private SubscribePayload createNewSubScribePayLoad(final String billingKey, final Integer amount,
		final String orderId) {
		SubscribePayload payload = new SubscribePayload();
		payload.billingKey = billingKey;
		payload.orderName = ORDER_NAME;
		payload.price = amount;
		payload.user = new User();
		payload.orderId = orderId;
		return payload;
	}

	private Subscribe createNewSubScribe(final CardInfo cardInfo, final Long userId) {
		Subscribe subscribe = new Subscribe();
		subscribe.orderName = ORDER_NAME;
		subscribe.subscriptionId = "" + (System.currentTimeMillis() / 1000);
		subscribe.pg = PG;
		subscribe.cardNo = cardInfo.cardNumber();
		subscribe.cardPw = cardInfo.cardPassword();
		subscribe.cardExpireYear = cardInfo.cardExpireYear();
		subscribe.cardExpireMonth = cardInfo.cardExpireMonth();
		subscribe.cardIdentityNo = cardInfo.birthDate();
		subscribe.user = new User();
		subscribe.user.id = String.valueOf(userId);
		return subscribe;
	}

	private Bootpay bootPaySetUp() {
		Bootpay bootpay = new Bootpay(restApplicationId, privateKey);
		acquireAccessToken(bootpay);
		return bootpay;
	}

	private void acquireAccessToken(final Bootpay bootpay) {
		try {
			bootpay.getAccessToken();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
