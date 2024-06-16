package com.flab.wesharepay.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;

import com.flab.core.entity.PayResult;
import com.flab.core.entity.Payment;

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
	public String requestBillingKey(final CardEnrollRequest cardEnrollRequest, final Long userId) {
		Bootpay bootpay = bootPaySetUp();

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

	@Override
	public PayResult requestPayment(final String billingKey, final Integer amount, final Payment payment) {
		Bootpay bootpay = bootPaySetUp();

		SubscribePayload payload = createNewSubScribePayLoad(billingKey, amount, String.valueOf(payment.getId()));

		try {
			HashMap<String, Object> res = bootpay.requestSubscribe(payload);
			JSONObject json = new JSONObject(res);
			if (res.get("error_code") == null) {
				return PayResult.ofSuccessfulPayResult(payment, json.toString());
			} else {
				return PayResult.ofRejectedPayResult(payment, json.toString());
			}
		} catch (Exception e) {
			log.error("결제 실패 orderId : {} 결제 요청중 예외 발생 ", payment.getId(), e);
			return PayResult.ofErrorOccurPayResult(payment, e.getMessage());
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

	private Subscribe createNewSubScribe(final CardEnrollRequest cardEnrollRequest, final Long userId) {
		Subscribe subscribe = new Subscribe();
		subscribe.orderName = ORDER_NAME;
		subscribe.subscriptionId = "" + (System.currentTimeMillis() / 1000);
		subscribe.pg = PG;
		subscribe.cardNo = cardEnrollRequest.cardNumber();
		subscribe.cardPw = cardEnrollRequest.cardPw();
		subscribe.cardExpireYear = cardEnrollRequest.cardExpireYear();
		subscribe.cardExpireMonth = cardEnrollRequest.cardExp ireMonth();
		subscribe.cardIdentityNo = cardEnrollRequest.birthDate();
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
