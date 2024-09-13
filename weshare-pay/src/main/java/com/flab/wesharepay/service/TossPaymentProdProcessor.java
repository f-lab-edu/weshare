package com.flab.wesharepay.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.wesharepay.dto.TossErrorResponse;
import com.flab.wesharepay.dto.TossPaymentRequest;
import com.flab.wesharepay.exception.IssueBillingKeyException;
import com.flab.wesharepay.exception.PaymentException;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("prod")
@Component
@RequiredArgsConstructor
public class TossPaymentProdProcessor implements TossPaymentProcessor {
	private static final String ISSUE_URL = "https://api.tosspayments.com/v1/billing/authorizations/issue";
	private static final String PAY_URL = "https://api.tosspayments.com/v1/billing/payments";

	private final ObjectMapper objectMapper;

	@Value("${pay.toss.secret_key}")
	private String secretKey;
	private String decodedSecretKey;

	@PostConstruct
	public void initAuthString() {
		this.decodedSecretKey =
			"Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public JSONObject requestBillingKey(final String customerKey, final String authKey) throws IOException {
		JSONObject requestData = new JSONObject(Map.of("customerKey", customerKey, "authKey", authKey));
		JSONObject response = sendRequest(requestData, ISSUE_URL);

		log.info("response : {}", response.toJSONString());
		if (response.containsKey("code")) {
			TossErrorResponse tossErrorResponse = new TossErrorResponse(response);
			log.error("빌링키 이슈 중 에러발생. code : {} , msg : {}", tossErrorResponse.getCode(),
				tossErrorResponse.getMessage());
			throw new IssueBillingKeyException(tossErrorResponse.getCode());
		}
		return response;
	}

	@Override
	public JSONObject requestPayment(String billingKey, TossPaymentRequest tossPaymentRequest) throws
		IOException {
		String paymentRequestJson = objectMapper.writeValueAsString(tossPaymentRequest);
		log.info("결제요청 - 빌링키 : {}, 파라미터 : {}", billingKey, paymentRequestJson);
		JSONObject response = sendRequest(paymentRequestJson, getPayUrl(billingKey));

		log.info("response : {}", response.toJSONString());
		if (response.containsKey("code")) {
			log.error("결제 중 에러발생. code : {} , msg : {}", response.get("code"),
				response.get("message"));
			throw new PaymentException(response.toJSONString());
		}
		return response;
	}

	private JSONObject sendRequest(JSONObject requestData, String urlString) throws IOException {
		return sendRequest(requestData.toJSONString(), urlString);
	}

	private JSONObject sendRequest(String requestData, String urlString) throws IOException {
		HttpURLConnection connection = createConnection(urlString);
		try (OutputStream os = connection.getOutputStream()) {
			os.write(requestData.getBytes(StandardCharsets.UTF_8));
		}

		try (InputStream responseStream = connection.getResponseCode() == 200 ? connection.getInputStream() :
			connection.getErrorStream();
			 Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8)) {
			return (JSONObject)new JSONParser().parse(reader);
		} catch (Exception e) {
			//여기서 발생할 수 있는 에러는 네트워크 에러, 파싱 에러 등등
			log.error("PG사 요청 내부 시스템 에러", e);
			JSONObject errorResponse = new JSONObject();
			errorResponse.put("error", "Error reading response");
			return errorResponse;
		}
	}

	private String getPayUrl(String billingKey) {
		return PAY_URL + "/" + billingKey;
	}

	private HttpURLConnection createConnection(String urlString) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestProperty("Authorization", decodedSecretKey);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		return connection;
	}
}
