package com.flab.weshare.domain.auth.controller;

import static com.flab.weshare.domain.utils.TestUtil.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.epages.restdocs.apispec.HeaderDescriptorWithType;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;
import com.flab.weshare.domain.auth.dto.LoginRequest;
import com.flab.weshare.domain.base.BaseControllerTest;
import com.flab.weshare.exception.ErrorCode;
import com.flab.weshare.utils.jwt.JwtProperties;

public class AuthControllerTest extends BaseControllerTest {
	LoginRequest rightloginRequest = new LoginRequest(EMAIL, PASSWORD);
	LoginRequest wrongPasswordLoginRequest = new LoginRequest(EMAIL, PASSWORD + "1");

	FieldDescriptors loginRequest = new FieldDescriptors()
		.and(fieldWithPath("email").description("로그인 이메일"))
		.and(fieldWithPath("password").description("비밀번호"));

	FieldDescriptors basicResponseFields = new FieldDescriptors()
		.and(fieldWithPath("success").type(SimpleType.BOOLEAN).description("반환 성공여부"))
		.and(subsectionWithPath("data").type(OBJECT).description("반환 데이터").optional())
		.and(fieldWithPath("errorMessage").type(OBJECT).description("에러 메시지").optional());

	FieldDescriptors loginResponse = new FieldDescriptors()
		.and(fieldWithPath("success").type(SimpleType.BOOLEAN).description("반환 성공여부"))
		.and(subsectionWithPath("data").type(OBJECT).description("반환 데이터").optional())
		.and(fieldWithPath("errorMessage").type(OBJECT).description("에러 메시지").optional())
		.and(fieldWithPath("data.accessToken").description("jwt access token"))
		.and(fieldWithPath("data.refreshToken").description("jwt refresh token"));

	FieldDescriptors testResponse = new FieldDescriptors()
		.and(fieldWithPath("success").type(SimpleType.BOOLEAN).description("반환 성공여부"))
		.and(subsectionWithPath("data").type(SimpleType.STRING).description("access token").optional())
		.and(fieldWithPath("errorMessage").type(OBJECT).description("에러 메시지").optional());

	HeaderDescriptorWithType refreshTokenHeader =
		new HeaderDescriptorWithType("Authorization")
			.type(SimpleType.STRING)
			.description("jwt Refresh token");

	@DisplayName("로그인 성공시 액세스 토큰과 리프레시 토큰 반환")
	@Test
	void login_success() throws Exception {
		mockMvc.perform(post("/api/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(rightloginRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.accessToken").exists())
			.andExpect(jsonPath("$.data.refreshToken").exists())
			.andDo(MockMvcRestDocumentationWrapper.document("로그인 성공",
				preRequestProcessorWithAuthorization,
				preResponseProcessor
				, ResourceDocumentation.resource(
					ResourceSnippetParameters.builder()
						.tag("인증 API")
						.description("로그인")
						.requestFields(loginRequest)
						.responseFields(loginResponse)
						.build()
				)));
	}

	@DisplayName("로그인 실패 - 비밀번호 오류")
	@Test
	void login_fail() throws Exception {
		mockMvc.perform(post("/api/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(wrongPasswordLoginRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(
				jsonPath("$.errorResponse.errorCode").value(ErrorCode.WRONG_PASSWORD.getErrorCode()))
			.andExpect(
				jsonPath("$.errorResponse.errorMessage").value(ErrorCode.WRONG_PASSWORD.getErrorMessage()));
	}

	@DisplayName("로그아웃 성공시 ok 반환")
	@Test
	void logout_success() throws Exception {
		mockMvc.perform(post("/api/logout")
				.header(JwtProperties.HEADER, REFRESH_TOKEN))
			.andExpect(status().isOk())
			.andDo(MockMvcRestDocumentationWrapper.document("로그아웃 성공",
				preRequestProcessorWithAuthorization,
				preResponseProcessor
				, ResourceDocumentation.resource(
					ResourceSnippetParameters.builder()
						.tag("인증 API")
						.description("로그아웃")
						.requestHeaders(refreshTokenHeader)
						.responseFields(basicResponseFields)
						.build()
				)));
	}

	@DisplayName("logout 요청시 Authorization 헤더가 존재하지 않으면 401 반환")
	@Test
	void logout_fail() throws Exception {
		mockMvc.perform(post("/api/logout"))
			.andExpect(status().isUnauthorized());
	}

	@DisplayName("정상적인 refresh token으로 reissue요청시 access토큰과 refresh 토큰 재발급 ")
	@Test
	void reIssue_success() throws Exception {
		mockMvc.perform(post("/api/reissue")
				.header(JwtProperties.HEADER, REFRESH_TOKEN))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.accessToken").exists())
			.andExpect(jsonPath("$.data.refreshToken").exists())
			.andDo(MockMvcRestDocumentationWrapper.document("재발급 성공",
				preRequestProcessorWithAuthorization,
				preResponseProcessor
				, ResourceDocumentation.resource(
					ResourceSnippetParameters.builder()
						.tag("인증 API")
						.description("jwt토큰 재발급")
						.requestHeaders(refreshTokenHeader)
						.responseFields(loginResponse)
						.build()
				)));
	}

	@DisplayName("reissue 요청시 Authorization 헤더가 존재하지 않으면 401 반환")
	@Test
	void reIssue_fail() throws Exception {
		mockMvc.perform(post("/api/reissue"))
			.andExpect(status().isUnauthorized());
	}
}
