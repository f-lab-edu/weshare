package com.flab.weshare.domain.user.controller;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ParameterDescriptorWithType;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.flab.weshare.domain.base.BaseControllerTest;
import com.flab.weshare.domain.user.dto.SignUpRequest;

class UserControllerTest extends BaseControllerTest {
	SignUpRequest signUpRequest = new SignUpRequest("test@email.com", "fffdfdf2@", "test", "01011111111");

	FieldDescriptors singnUpRequestFields =
		new FieldDescriptors()
			.and(fieldWithPath("email").type(STRING).description("멤버 이메일"))
			.and(fieldWithPath("nickName").type(STRING).description("멤버 닉네임"))
			.and(fieldWithPath("password").type(STRING).description("멤버 패스워드"))
			.and(fieldWithPath("telephone").type(STRING).description("전화번호"));

	FieldDescriptors responseFields =
		new FieldDescriptors().and(fieldWithPath("success").type(BOOLEAN).description("반환 성공여부"))
			.and(fieldWithPath("data").type(OBJECT).description("반환 데이터").optional())
			.and(fieldWithPath("errorMessage").type(OBJECT).description("에러 메시지").optional());

	FieldDescriptors duplicatedCheckFields =
		responseFields
			.and(fieldWithPath("data.target").description("중복 확인 필드 값"))
			.and(fieldWithPath("data.isDuplicated").description("중복 여부"));

	@Test
	void dd() throws Exception {
		mockMvc.perform(get("/no"))
			.andDo(print());
	}

	@Test
	void signup_test() throws Exception {
		mockMvc.perform(post("/api/v1/user")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signUpRequest)))
			.andExpect(status().isCreated())
			.andDo(MockMvcRestDocumentationWrapper.document("회원 가입 성공",
				preRequestProcessor,  // 한 줄로 출력되는 json에 pretty 포멧 적용
				preResponseProcessor
				,
				ResourceDocumentation.resource(
					ResourceSnippetParameters.builder()
						.tag("유저 API")
						.description("회원 가입")
						.requestFields(singnUpRequestFields)
						.responseFields(responseFields)
						.build()
				)));
	}

	@Test
	void email_test() throws Exception {
		String email = "unique@gmail.com";

		mockMvc.perform(get("/api/v1/user/check-email")
				.param("email", email)
			)
			.andExpect(status().isOk())
			.andExpectAll(
				jsonPath("$.success").value("true"),
				jsonPath("$.data.target").value(email),
				jsonPath("$.data.isDuplicated").value(false)
			)
			.andDo(print())
			.andDo(MockMvcRestDocumentationWrapper.document("이메일 중복 x",
				preRequestProcessor,
				preResponseProcessor
				,
				ResourceDocumentation.resource(
					ResourceSnippetParameters.builder()
						.tag("유저 API")
						.description("이메일 중복확인")
						.queryParameters(new ParameterDescriptorWithType("email").description("중복확인 이메일"))
						.responseFields(duplicatedCheckFields)
						.build()
				)));
	}

	@Test
	void nickname_test() throws Exception {
		String nickName = "꾀돌이";

		mockMvc.perform(get("/api/v1/user/check-nickname")
				.param("nickname", nickName)
			)
			.andExpect(status().isOk())
			.andExpectAll(
				jsonPath("$.success").value("true"),
				jsonPath("$.data.target").value(nickName),
				jsonPath("$.data.isDuplicated").value(false)
			)
			.andDo(print())
			.andDo(MockMvcRestDocumentationWrapper.document("닉네임 중복 x",
				preRequestProcessor,
				preResponseProcessor
				,
				ResourceDocumentation.resource(
					ResourceSnippetParameters.builder()
						.tag("유저 API")
						.description("닉네임 중복확인")
						.queryParameters(new ParameterDescriptorWithType("nickname").description("중복확인 닉네임"))
						.responseFields(duplicatedCheckFields)
						.build())
			));
	}
}
