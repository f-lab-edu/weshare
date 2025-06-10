package com.flab.weshare.domain.pay.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.core.entity.BillingKey;
import com.flab.core.entity.User;
import com.flab.core.infra.BillingKeyRepository;
import com.flab.core.infra.UserRepository;
import com.flab.weshare.config.ObjectMapperConfig;
import com.flab.weshare.domain.base.BaseServiceTest;

class CardServiceTest extends BaseServiceTest {
	private static final String MID = "tosspayments";
	private static final String CUSTOMER_KEY = "aENcQAtPdYbTjGhtQnNVj";
	private static final String AUTHENTICATED_AT = "2020-09-25T14:38:41+09:00";
	private static final String METHOD = "카드";
	private static final String BILLING_KEY = "Z_t5vOvQxrj4499PeiJcjen28-V2RyqgYTwN44Rdzk0=";
	private static final String ISSUER_CODE = "61";
	private static final String ACQUIRER_CODE = "31";
	private static final String CARD_NUMBER = "12345678123";
	private static final String CARD_TYPE = "신용";
	private static final String OWNER_TYPE = "개인";
	private static final String CARD_COMPANY = "현대";
	private static final String clientId = "550e8400-e29b-41d4-a716-446655440000";

	@Mock
	private UserRepository userRepository;

	@Mock
	private BillingKeyRepository billingKeyRepository;

	@Spy
	private ObjectMapper objectMapper = new ObjectMapperConfig().objectMapper();

	@InjectMocks
	private CardService cardService;

	@Test
	void enrollBillingKey() throws Exception {
		JSONObject billingKeyResponse = getJsonObject();
		BillingKey translatedBillingKey = objectMapper.readValue(billingKeyResponse.toJSONString(), BillingKey.class);

		BillingKey billingKey = BillingKey.builder()
			.id(1L)
			.mId(MID)
			.authenticatedAt(OffsetDateTime.parse(AUTHENTICATED_AT))
			.method(METHOD)
			.billingKey(BILLING_KEY)
			.cardCompany(CARD_COMPANY)
			.cardNumber(CARD_NUMBER)
			.build();

		User user = User.builder()
			.id(1L)
			.clientId(clientId)
			.build();

		doCallRealMethod().when(objectMapper).readValue(billingKeyResponse.toJSONString(), BillingKey.class);
		when(objectMapper.readValue(billingKeyResponse.toJSONString(), BillingKey.class)).thenReturn(billingKey);
		when(userRepository.findByClientId(anyString())).thenReturn(Optional.ofNullable(user));
		when(billingKeyRepository.save(billingKey)).thenReturn(billingKey);

		// when
		Long result = cardService.enrollBillingKey(billingKeyResponse);

		// then
		assertThat(result).isEqualTo(1L);
		assertThat(billingKey.getUser()).isEqualTo(user);
		verify(billingKeyRepository, times(1)).save(billingKey);
	}

	private @NotNull JSONObject getJsonObject() {
		JSONObject billingKeyResponse = new JSONObject();
		JSONObject card = new JSONObject();
		billingKeyResponse.put("mId", MID);
		billingKeyResponse.put("customerKey", CUSTOMER_KEY);
		billingKeyResponse.put("authenticatedAt", AUTHENTICATED_AT);
		billingKeyResponse.put("method", METHOD);
		billingKeyResponse.put("billingKey", BILLING_KEY);
		card.put("issuerCode", ISSUER_CODE);
		card.put("acquirerCode", ACQUIRER_CODE);
		card.put("number", CARD_NUMBER);
		card.put("cardType", CARD_TYPE);
		card.put("ownerType", OWNER_TYPE);
		billingKeyResponse.put("card", card);
		billingKeyResponse.put("cardCompany", CARD_COMPANY);
		billingKeyResponse.put("cardNumber", CARD_NUMBER);
		return billingKeyResponse;
	}
}
