package com.flab.weshare.domain.party.controller;

import static com.flab.weshare.domain.utils.TestUtil.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.flab.weshare.domain.base.BaseControllerTest;
import com.flab.weshare.domain.party.dto.ModifyPartyRequest;
import com.flab.weshare.domain.party.dto.PartyCreationRequest;
import com.flab.weshare.domain.party.dto.PartyJoinRequest;
import com.flab.weshare.exception.ErrorCode;
import com.flab.weshare.utils.jwt.JwtProperties;

class PartyControllerTest extends BaseControllerTest {
	@DisplayName("파티를 생성할 수 있다.")
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
			.andExpect(jsonPath("$.data.id").exists());
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

	@DisplayName("파티의 비밀번호와 정원을 수정할 수 있다.")
	@Test
	void successPartyUpdate() throws Exception {
		ModifyPartyRequest modifyPartyRequest = new ModifyPartyRequest(4, "asddff222");

		mockMvc.perform(put("/api/v1/party/" + savedParty.getId())
				.header(JwtProperties.HEADER, ACCESS_TOKEN)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(modifyPartyRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value("true"));
	}

	@DisplayName("현재 파티의 인원수보다 적게 정원수를 변경할 수 없다.")
	@Test
	void failPartyUpdate() throws Exception {
		ModifyPartyRequest modifyPartyRequest = new ModifyPartyRequest(2, "asddff222");
		mockMvc.perform(put("/api/v1/party/" + savedParty.getId())
				.header(JwtProperties.HEADER, ACCESS_TOKEN)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(modifyPartyRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorResponse.errorCode").value(ErrorCode.INSUFFICIENT_CAPACITY.getErrorCode()))
			.andExpect(
				jsonPath("$.errorResponse.errorMessage").value(ErrorCode.INSUFFICIENT_CAPACITY.getErrorMessage()));
	}

	@DisplayName("파티요청을 생성할 수 있다.")
	@Test
	void success_party_join_creation() throws Exception {
		PartyJoinRequest partyCreationRequest = new PartyJoinRequest(savedParty.getId());

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

}
