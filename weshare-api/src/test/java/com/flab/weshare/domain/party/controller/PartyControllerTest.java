package com.flab.weshare.domain.party.controller;

import static com.flab.weshare.domain.utils.TestUtil.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultHandler;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.epages.restdocs.apispec.HeaderDescriptorWithType;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ParameterDescriptorWithType;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;
import com.flab.weshare.domain.base.BaseControllerTest;
import com.flab.weshare.domain.party.dto.ModifyPartyRequest;
import com.flab.weshare.domain.party.dto.PartyCreationRequest;
import com.flab.weshare.domain.party.dto.PartyJoinRequest;
import com.flab.weshare.exception.ErrorCode;
import com.flab.weshare.utils.jwt.JwtProperties;

class PartyControllerTest extends BaseControllerTest {
	HeaderDescriptorWithType jwtHeader =
		new HeaderDescriptorWithType("Authorization")
			.type(SimpleType.STRING)
			.description("jwt token");

	FieldDescriptors partyCreationSchema = new FieldDescriptors()
		.and(fieldWithPath("ottId").description("신청하는 파티의 Ott id"))
		.and(fieldWithPath("ottAccountId").description("ott 계정의 id"))
		.and(fieldWithPath("ottAccountPassword").description("ott 계정의 비밀번호"))
		.and(fieldWithPath("capacity").description("신청하는 파티의 정원 수"));

	FieldDescriptors partyModificationSchema = new FieldDescriptors()
		.and(fieldWithPath("capacity").description("선택한 파티의 수정하고자 하는 정원"))
		.and(fieldWithPath("password").description("ott 계정의 수정하고자 하는 비밀번호"));

	FieldDescriptors basicResponseFields = new FieldDescriptors()
		.and(fieldWithPath("success").type(SimpleType.BOOLEAN).description("반환 성공여부"))
		.and(subsectionWithPath("data").type(OBJECT).description("반환 데이터").optional())
		.and(fieldWithPath("errorMessage").type(OBJECT).description("에러 메시지").optional());

	FieldDescriptors getPartyListFields = new FieldDescriptors()
		.and(fieldWithPath("success").type(SimpleType.BOOLEAN).description("반환 성공여부"))
		.and(fieldWithPath("data").type(OBJECT).description("반환 데이터").optional())
		.and(subsectionWithPath("data.links").description("현재 페이지 링크"))
		.and(fieldWithPath("data.leadingParties").description("현재 파티장으로 있는 파티 목록"))
		.and(fieldWithPath("data.leadingParties[].partyId").description("파티 id").optional())
		.and(fieldWithPath("data.leadingParties[].startDate").description("시작 날짜").optional())
		.and(fieldWithPath("data.leadingParties[].ottName").description("파티의 ott 종류").optional())
		.and(subsectionWithPath("data.leadingParties[].links").description("각 파티 상세 링크"))
		.and(fieldWithPath("data.participatingParties").description("현재 파티원으로 있는 파티 목록"))
		.and(fieldWithPath("data.participatingParties[].partyCapsuleId").description("참여한 파티의 자리의 id").optional())
		.and(fieldWithPath("data.participatingParties[].startDate").description("시작 날짜").optional())
		.and(fieldWithPath("data.participatingParties[].ottName").description("파티의 ott 종류").optional())
		.and(subsectionWithPath("data.participatingParties[].links").type(ARRAY)
			.description("참여하고 있는 파티 상세 링크")
			.optional())
		.and(fieldWithPath("errorMessage").type(OBJECT).description("에러 메시지").optional());

	FieldDescriptors getPartyDetails = new FieldDescriptors()
		.and(fieldWithPath("success").type(SimpleType.BOOLEAN).description("반환 성공여부"))
		.and(fieldWithPath("data").type(OBJECT).description("반환 데이터").optional())
		.and(fieldWithPath("data.partyId").description("선택한 파티의 id"))
		.and(fieldWithPath("data.startDate").description("파티 생성 날짜"))
		.and(fieldWithPath("data.ottName").description("파티의 ott명"))
		.and(fieldWithPath("data.ottAccountId").description("ott 계정 id"))
		.and(fieldWithPath("data.participants[]").description("파티의 ott명"))
		.and(fieldWithPath("data.participants[].nickName").type(STRING).description("참석자 닉네임").optional())
		.and(fieldWithPath("data.participants[].joinDate").type(STRING).description("참석자의 파티 참가 날짜").optional())
		.and(fieldWithPath("data.participants[].status").type(STRING).description("참석 상태"))
		.and(fieldWithPath("errorMessage").type(OBJECT).description("에러 메시지").optional());

	FieldDescriptors getPartyCapsuleDetails = new FieldDescriptors()
		.and(fieldWithPath("success").type(SimpleType.BOOLEAN).description("반환 성공여부"))
		.and(fieldWithPath("data").type(OBJECT).description("반환 데이터").optional())
		.and(fieldWithPath("data.partyCapsuleId").description("참여한 파티 좌석의 id"))
		.and(fieldWithPath("data.startDate").description("파티 참가 날짜"))
		.and(fieldWithPath("data.expirationDate").description("만료 날짜"))
		.and(fieldWithPath("data.ottName").description("참가하고 있는 파티의 ott명"))
		.and(fieldWithPath("data.cancelReservation").description("파티 해지 예약 여부"))
		.and(fieldWithPath("data.status").description("참석 상태"))
		.and(fieldWithPath("errorMessage").type(OBJECT).description("에러 메시지").optional());

	@DisplayName("파티 생성 api")
	@Test
	void success_party_creation() throws Exception {
		PartyCreationRequest partyCreationRequest = new PartyCreationRequest(
			savedOtt.getId(), OTT_ACCOUNT_ID, OTT_PASSWORD, PARTY_MAXIMUM_CAPACITY);
		mockMvc.perform(post("/api/v1/party")
				.header(JwtProperties.HEADER, ACCESS_TOKEN)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(partyCreationRequest)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.success").value("true"))
			.andExpect(jsonPath("$.data.id").exists())
			.andDo(print())
			.andDo(MockMvcRestDocumentationWrapper.document("파티 생성",
				preRequestProcessorWithAuthorization,
				preResponseProcessor
				, ResourceDocumentation.resource(
					ResourceSnippetParameters.builder()
						.tag("파티 API")
						.description("파티를 생성할 수 있다.")
						.requestFields(partyCreationSchema)
						.responseFields(basicResponseFields)
						.build()
				)));
	}

	@DisplayName("파티의 정원이 요청한 Ott의 최대정원보다 높을시 실패한다.")
	@Test
	void fail_party_creation() throws Exception {
		PartyCreationRequest partyCreationRequest = new PartyCreationRequest(
			savedOtt.getId(), OTT_ACCOUNT_ID, OTT_PASSWORD, MAXIMUM_CAPACITY + 2);

		mockMvc.perform(post("/api/v1/party")
				.header(JwtProperties.HEADER, ACCESS_TOKEN)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(partyCreationRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value("false"))
			.andExpect(jsonPath("$.errorResponse.errorCode").value(ErrorCode.INVALID_CAPACITY.getErrorCode()))
			.andExpect(jsonPath("$.errorResponse.errorMessage").value(ErrorCode.INVALID_CAPACITY.getErrorMessage()));
	}

	@DisplayName("파티 정보 수정 api")
	@Test
	void successPartyUpdate() throws Exception {
		ModifyPartyRequest modifyPartyRequest = new ModifyPartyRequest(4, "asddff222");

		mockMvc.perform(put("/api/v1/party/{partyId}", savedParty.getId().toString())
				.header(JwtProperties.HEADER, ACCESS_TOKEN)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(modifyPartyRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value("true"))
			.andDo(MockMvcRestDocumentationWrapper.document("파티 수정",
				preRequestProcessorWithAuthorization,
				preResponseProcessor
				, ResourceDocumentation.resource(
					ResourceSnippetParameters.builder()
						.tag("파티 API")
						.description("파티의 정원과 ott 계정의 비밀번호를 변경할 수 있다.")
						.pathParameters(
							new ParameterDescriptorWithType("partyId")
								.description("수정하려고하는 파티의 id")
						)
						.requestFields(partyModificationSchema)
						.responseFields(basicResponseFields)
						.build()
				)));
		;
	}

	@DisplayName("현재 파티의 인원수보다 적게 정원수를 변경할 수 없다.")
	@Test
	void failPartyUpdate() throws Exception {
		ModifyPartyRequest modifyPartyRequest = new ModifyPartyRequest(1, "asddff222");
		mockMvc.perform(put("/api/v1/party/" + savedParty.getId())
				.header(JwtProperties.HEADER, ACCESS_TOKEN)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(modifyPartyRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorResponse.errorCode").value(ErrorCode.INSUFFICIENT_CAPACITY.getErrorCode()))
			.andExpect(
				jsonPath("$.errorResponse.errorMessage").value(ErrorCode.INSUFFICIENT_CAPACITY.getErrorMessage()));
	}

	@DisplayName("파티 가입 요청 api")
	@Test
	void success_party_join_creation() throws Exception {
		PartyJoinRequest partyCreationRequest = new PartyJoinRequest(savedOtt.getId());

		mockMvc.perform(post("/api/v1/party/join")
				.header(JwtProperties.HEADER, ACCESS_TOKEN)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(partyCreationRequest)))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.success").value("true"))
			.andExpect(jsonPath("$.data.id").exists());
	}

	@DisplayName("파티요청을 생성하기 위해선 유효한 ottId와 userId가 필요하다.")
	@Test
	void success_party_join_fail() throws Exception {
		PartyJoinRequest partyCreationRequest = new PartyJoinRequest(1233L); //DB에 존재하지않는 ottId

		mockMvc.perform(post("/api/v1/party/join")
				.header(JwtProperties.HEADER, ACCESS_TOKEN)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(partyCreationRequest)))
			.andDo(print())
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.success").value("false"))
			.andExpect(jsonPath("$.errorResponse.errorCode").value(ErrorCode.DATA_INTEGRITY_VIOLATION.getErrorCode()))
			.andExpect(
				jsonPath("$.errorResponse.errorMessage").value(ErrorCode.DATA_INTEGRITY_VIOLATION.getErrorMessage()));
	}

	@DisplayName("가입 중인 파티 조회 api")
	@Test
	void success_get_party_list() throws Exception {
		String accessToken = JwtProperties.TOKEN_PREFIX + jwtUtil.createAccessToken(8L);
		mockMvc
			.perform(get("/api/v1/party/my")
				.header(JwtProperties.HEADER, accessToken)
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true)) // success 필드 검증
			.andExpect(jsonPath("$.data.links[0].rel").value("self")) // links 배열의 첫 번째 요소 rel 값 검증
			.andExpect(jsonPath("$.data.links[0].href").value("http://localhost:8080/api/v1/party/my")) // href 값 검증

			// 참여중인 파티 (participatingParties)
			.andExpect(jsonPath("$.data.participatingParties[0].partyCapsuleId").value(5)) // 첫 번째 파티CapsuleId 검증
			.andExpect(jsonPath("$.data.participatingParties[0].startDate").value("2024-03-26")) // 첫 번째 startDate 검증
			.andExpect(jsonPath("$.data.participatingParties[0].ottName").value("왓챠")) // 첫 번째 ottName 검증
			.andExpect(jsonPath("$.data.participatingParties[0].links[0].rel").value("self")) // 첫 번째 파티 링크 rel 검증
			.andExpect(jsonPath("$.data.participatingParties[0].links[0].href").value(
				"http://localhost:8080/api/v1/party/participated/5")) // 첫 번째 파티 링크 href 검증

			.andExpect(jsonPath("$.data.participatingParties[1].partyCapsuleId").value(6)) // 두 번째 파티CapsuleId 검증
			.andExpect(jsonPath("$.data.participatingParties[1].startDate").value("2024-03-26")) // 두 번째 startDate 검증
			.andExpect(jsonPath("$.data.participatingParties[1].ottName").value("넷플릭스")) // 두 번째 ottName 검증
			.andExpect(jsonPath("$.data.participatingParties[1].links[0].rel").value("self")) // 두 번째 파티 링크 rel 검증
			.andExpect(jsonPath("$.data.participatingParties[1].links[0].href").value(
				"http://localhost:8080/api/v1/party/participated/6")) // 두 번째 파티 링크 href 검증

			// 리딩 중인 파티 (leadingParties)
			.andExpect(jsonPath("$.data.leadingParties[0].partyId").value(1)) // 리딩 파티Id 검증
			.andExpect(jsonPath("$.data.leadingParties[0].startDate").value("2024-03-27")) // 리딩 파티 startDate 검증
			.andExpect(jsonPath("$.data.leadingParties[0].ottName").value("아마존 프라임")) // 리딩 파티 ottName 검증
			.andExpect(jsonPath("$.data.leadingParties[0].links[0].rel").value("self")) // 리딩 파티 링크 rel 검증
			.andExpect(jsonPath("$.data.leadingParties[0].links[0].href").value(
				"http://localhost:8080/api/v1/party/1")) // 리딩 파티 링크 href 검증
			.andDo(print())
			.andDo(responsePrettyPrint())
			.andDo(MockMvcRestDocumentationWrapper.document("참여중인 모든 파티 조회",
				preRequestProcessorWithAuthorization,
				preResponseProcessor
				, ResourceDocumentation.resource(
					ResourceSnippetParameters.builder()
						.tag("파티 API")
						.description("가입 되어있는 모든 파티의 목록을 조회 할 수 있다.")
						.responseFields(getPartyListFields)
						.build()
				)));
	}

	@DisplayName("개설한 파티 개별 조회 api")
	@Test
	void success_get_party_info() throws Exception {
		String accessToken = JwtProperties.TOKEN_PREFIX + jwtUtil.createAccessToken(8L);
		mockMvc.perform(get("/api/v1/party/{partyId}", 1)
				.header(JwtProperties.HEADER, accessToken)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value("true"))
			.andExpect(jsonPath("$.data.participants", hasSize(4)))
			.andExpect(jsonPath("$.data.partyId").value(1))
			.andExpect(jsonPath("$.data.participants[0].status").value("empty"))
			.andExpect(jsonPath("$.data.participants[1].status").value("empty"))
			.andExpect(jsonPath("$.data.participants[2].nickName").value("사람5"))
			.andExpect(jsonPath("$.data.participants[2].joinDate").value("2024-03-26"))
			.andExpect(jsonPath("$.data.participants[2].status").value("occupied"))
			.andExpect(jsonPath("$.data.participants[3].nickName").value("사람6"))
			.andExpect(jsonPath("$.data.participants[3].joinDate").value("2024-03-26"))
			.andExpect(jsonPath("$.data.participants[3].status").value("occupied"))
			.andExpect(jsonPath("$.data.startDate").value("2024-03-27"))
			.andExpect(jsonPath("$.data.ottName").value("아마존 프라임"))
			.andExpect(jsonPath("$.data.ottAccountId").value("asdfdf"))
			.andDo(responsePrettyPrint())
			.andDo(MockMvcRestDocumentationWrapper.document("파티 상세 조회",
				preRequestProcessorWithAuthorization,
				preResponseProcessor
				, ResourceDocumentation.resource(
					ResourceSnippetParameters.builder()
						.tag("파티 API")
						.description("파티를 상세조회")
						.pathParameters(
							new ParameterDescriptorWithType("partyId")
								.description("조회하고자 하는 파티의 id")
						)
						.responseFields(getPartyDetails)
						.build()
				)));
	}

	@DisplayName("참여중인 파티 개별 조회 api")
	@Test
	void success_get_party_capsule_info() throws Exception {
		String accessToken = JwtProperties.TOKEN_PREFIX + jwtUtil.createAccessToken(8L);
		mockMvc.perform(get("/api/v1/party/participated/{partyCapsuleId}", 5)
				.header(JwtProperties.HEADER, accessToken)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(responsePrettyPrint())
			.andDo(MockMvcRestDocumentationWrapper.document("파티 참석 상세 조회",
				preRequestProcessorWithAuthorization,
				preResponseProcessor
				, ResourceDocumentation.resource(
					ResourceSnippetParameters.builder()
						.tag("파티 API")
						.description("참석하고 있는 파티 정보 상세조회")
						.pathParameters(
							new ParameterDescriptorWithType("partyCapsuleId")
								.description("참석하고 있는 파티 정보의 id")
						)
						.responseFields(getPartyCapsuleDetails)
						.build()
				)));
	}

	@DisplayName("참여 중인 파티 중지 api")
	@Test
	void success_suspend_party_capsule_info() throws Exception {
		String accessToken = JwtProperties.TOKEN_PREFIX + jwtUtil.createAccessToken(8L);

		mockMvc.perform(delete("/api/v1/party/participated/{partyCapsuleId}", 5)
				.header(JwtProperties.HEADER, accessToken)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value("true"))
			.andDo(MockMvcRestDocumentationWrapper.document("참여 중인 파티 해지 API",
				preRequestProcessorWithAuthorization,
				preResponseProcessor
				, ResourceDocumentation.resource(
					ResourceSnippetParameters.builder()
						.tag("파티 API")
						.description("참여 중인 파티 해지")
						.pathParameters(
							new ParameterDescriptorWithType("partyCapsuleId")
								.description("해지 하고자 하는 참석 파티")
						)
						.responseFields(basicResponseFields)
						.build()
				)));
	}

	@DisplayName("ott 계정 조회 api")
	@Test
	void success_get_ott_account_info() throws Exception {
		String accessToken = JwtProperties.TOKEN_PREFIX + jwtUtil.createAccessToken(5L);

		mockMvc.perform(get("/api/v1/party/{partyId}/ottAccountInfo", 1)
				.header(JwtProperties.HEADER, accessToken)
				.queryParam("isLeader", "false")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value("true"))
			.andDo(MockMvcRestDocumentationWrapper.document("ott 계정 정보 이메일 전송요청 API",
				preRequestProcessorWithAuthorization,
				preResponseProcessor
				, ResourceDocumentation.resource(
					ResourceSnippetParameters.builder()
						.tag("파티 API")
						.description("ott 계정 정보 이메일 전송요청")
						.pathParameters(
							new ParameterDescriptorWithType("partyId")
								.description("Ott 계정 정보를 요청하는 파티 id")
						)
						.queryParameters(new ParameterDescriptorWithType("isLeader").description("파티장 여부"))
						.responseFields(basicResponseFields)
						.build()
				)));
	}

	@DisplayName("ott 계정 비밀번호 변경 api")
	@Test
	void change_ott_account_password() throws Exception {
		String accessToken = JwtProperties.TOKEN_PREFIX + jwtUtil.createAccessToken(8L);

		mockMvc.perform(put("/api/v1/party/{partyId}/changePassword", 1)
				.header(JwtProperties.HEADER, accessToken)
				.queryParam("changingPassword", "asdfffddccx")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value("true"))
			.andDo(MockMvcRestDocumentationWrapper.document("ott 계정 비밀번호 변경 API",
				preRequestProcessorWithAuthorization,
				preResponseProcessor
				, ResourceDocumentation.resource(
					ResourceSnippetParameters.builder()
						.tag("파티 API")
						.description("ott 계정 비밀번호 변경")
						.pathParameters(
							new ParameterDescriptorWithType("partyId")
								.description("파티 id")
						)
						.queryParameters(
							new ParameterDescriptorWithType("changingPassword").description("변경하고자하는 비밀번호"))
						.responseFields(basicResponseFields)
						.build()
				)));
	}

	private ResultHandler responsePrettyPrint() {
		return result -> {
			Object o = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8),
				Object.class);
			String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
			System.out.println(s);
		};
	}
}
