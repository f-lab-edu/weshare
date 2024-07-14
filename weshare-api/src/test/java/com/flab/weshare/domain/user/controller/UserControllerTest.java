package com.flab.weshare.domain.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.flab.weshare.domain.base.BaseControllerTest;
import com.flab.weshare.domain.user.dto.SignUpRequest;

class UserControllerTest extends BaseControllerTest {
	SignUpRequest signUpRequest = new SignUpRequest("test@email.com", "fffdfdf2@", "test", "01011111111");

	@Test
	void signup_test() throws Exception {
		mockMvc.perform(post("/api/v1/user")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signUpRequest)))
			.andExpect(status().isCreated());
	}
}
