package com.flab.wesharepay.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.flab.wesharepay.dto.TossPaymentRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile({"local", "test"})
@Component
public class TossPaymentLocalProcessor implements TossPaymentProcessor {
	@Override
	public JSONObject requestBillingKey(String customerKey, String authKey) throws IOException {
		JSONObject card = new JSONObject();
		card.put("ownerType", "개인");
		card.put("number", "37625112****222*");
		card.put("cardType", "신용");
		card.put("issuerCode", "71");
		card.put("acquirerCode", "71");

		JSONObject root = new JSONObject();
		root.put("authenticatedAt", OffsetDateTime.now(ZoneOffset.ofHours(9)).toString());
		root.put("customerKey", customerKey);
		root.put("method", "카드");
		root.put("mId", "tvivarepublica2");
		root.put("billingKey", getUUIDBase64());
		root.put("cardCompany", "롯데");
		root.put("cardNumber", "37625112****222*");
		root.put("card", card);

		log.info("reponse : {}", root.toJSONString());
		return root;

	}

	@Override
	public JSONObject requestPayment(String billingKey, TossPaymentRequest tossPaymentRequest) throws IOException {
		log.info("결제요청 - 빌링키 : {}, 파라미터 : {}", billingKey, tossPaymentRequest);

		JSONObject card = new JSONObject();
		card.put("issuerCode", "71");
		card.put("acquirerCode", "71");
		card.put("number", "51379200****061*");
		card.put("installmentPlanMonths", 0);
		card.put("isInterestFree", false);
		card.put("interestPayer", null);
		card.put("approveNo", "00000000");
		card.put("useCardPoint", false);
		card.put("cardType", "신용");
		card.put("ownerType", "개인");
		card.put("acquireStatus", "READY");
		card.put("amount", tossPaymentRequest.amount());

		// receipt
		JSONObject receipt = new JSONObject();
		receipt.put("url",
			"https://dashboard.tosspayments.com/receipt/redirection?transactionId=tviva20240213145434Rycs6&ref=PX");

		// checkout
		JSONObject checkout = new JSONObject();
		checkout.put("url",
			"https://api.tosspayments.com/v1/payments/y05n91dEvLex6BJGQOVDpgDQ0gDv0QVW4w2zNbgaYRMPoqmD/checkout");

		// 루트 오브젝트
		JSONObject root = new JSONObject();
		root.put("mId", "tosspayments");
		root.put("lastTransactionKey", "798780972A6B30495E2DAB97839D1199");
		root.put("paymentKey", "y05n91dEvLex6BJGQOVDpgDQ0gDv0QVW4w2zNbgaYRMPoqmD");
		root.put("orderId", tossPaymentRequest.orderId());
		root.put("orderName", tossPaymentRequest.orderName());
		root.put("taxExemptionAmount", 0);
		root.put("status", "DONE");
		root.put("requestedAt", OffsetDateTime.now(ZoneOffset.ofHours(9)).minusSeconds(1L).toString());
		root.put("approvedAt", OffsetDateTime.now(ZoneOffset.ofHours(9)).toString());
		root.put("useEscrow", false);
		root.put("cultureExpense", false);
		root.put("card", card);
		root.put("virtualAccount", null);
		root.put("transfer", null);
		root.put("mobilePhone", null);
		root.put("giftCertificate", null);
		root.put("cashReceipt", null);
		root.put("cashReceipts", null);
		root.put("discount", null);
		root.put("cancels", null);
		root.put("secret", null);
		root.put("type", "BILLING");
		root.put("easyPay", null);
		root.put("country", "KR");
		root.put("failure", null);
		root.put("isPartialCancelable", true);
		root.put("receipt", receipt);
		root.put("checkout", checkout);
		root.put("currency", "KRW");
		root.put("totalAmount", tossPaymentRequest.amount());
		root.put("balanceAmount", tossPaymentRequest.amount());
		root.put("suppliedAmount", 0);
		root.put("vat", 0);
		root.put("taxFreeAmount", 0);
		root.put("metadata", null);
		root.put("method", "카드");
		root.put("version", "2022-11-16");

		log.info("reponse : {}", root.toJSONString());
		return root;
	}

	private String getUUIDBase64() {
		UUID uuid = UUID.randomUUID();
		byte[] bytes = ByteBuffer.allocate(16)
			.putLong(uuid.getMostSignificantBits())
			.putLong(uuid.getLeastSignificantBits())
			.array();

		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}
}


