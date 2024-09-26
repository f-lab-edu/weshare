package com.flab.weshare.domain.pay.controller;

import static com.flab.weshare.domain.utils.TestUtil.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ParameterDescriptorWithType;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.flab.weshare.domain.base.BaseControllerTest;
import com.flab.wesharepay.service.TossPaymentProcessor;

class BillingKeyControllerTest extends BaseControllerTest {
	@MockBean
	private TossPaymentProcessor tossPaymentProcessor;

	FieldDescriptors fieldDescriptors = new FieldDescriptors().and(fieldWithPath("success").description("빌링키 발급 성공 여부"))
		.and(fieldWithPath("data").type(NUMBER).description("생성된 빌링키 번호"));

	@Test
	void success_Issue_BillingKey() throws Exception {
		JSONObject payResponse = getPayRespnose();
		when(tossPaymentProcessor.requestBillingKey(any(), any())).thenReturn(payResponse);

		mockMvc.perform(get("/api/v1/issue-billingKey")
				.param("customerKey", savedUser.getClientId())
				.param("authKey", "test_auth_key")
			)
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.success").value("true"))
			.andDo(print())
			.andDo(MockMvcRestDocumentationWrapper.document("빌링키 발급",
				preRequestProcessor,
				preResponseProcessor,
				ResourceDocumentation.resource(
					ResourceSnippetParameters.builder()
						.tag("빌링키 발급 API")
						.description("PG사 빌링키 발급 연동")
						.queryParameters(new ParameterDescriptorWithType("customerKey").description("고객 식별자")
							, new ParameterDescriptorWithType("authKey").description("PG사 인증 키"))
						.responseFields(fieldDescriptors)
						.build()
				)));
	}

	private JSONObject getPayRespnose() {
		JSONObject billingKeyResponse = new JSONObject();
		billingKeyResponse.put("mId", "tosspayments");
		billingKeyResponse.put("customerKey", savedUser.getClientId());
		billingKeyResponse.put("authenticatedAt", "2020-09-25T14:38:41+09:00");
		billingKeyResponse.put("method", "카드");
		billingKeyResponse.put("billingKey", "Z_t5vOvQxrj4499PeiJcjen28-V2RyqgYTwN44Rdzk0=");

		JSONObject card = new JSONObject();
		card.put("issuerCode", "61");
		card.put("acquirerCode", "31");
		card.put("number", "12345678123");
		card.put("cardType", "신용");
		card.put("ownerType", "개인");

		billingKeyResponse.put("card", card);
		billingKeyResponse.put("cardCompany", "현대");
		billingKeyResponse.put("cardNumber", "12345678123");

		return billingKeyResponse;
	}

}
