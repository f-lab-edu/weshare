package com.flab.weshare.domain.base;

import static com.flab.weshare.domain.utils.TestUtil.*;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.weshare.domain.user.repository.UserRepository;
import com.flab.weshare.utils.jwt.JwtUtil;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public abstract class BaseControllerTest {
	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	protected UserRepository userRepository;

	@Autowired
	JwtUtil jwtUtil;

	protected String ACCESS_TOKEN;
	protected String REFRESH_TOKEN;

	@BeforeEach
	void setUpLogin() {
		userRepository.save(savedUser);
		ACCESS_TOKEN = jwtUtil.createAccessToken(savedUser.getId());
		REFRESH_TOKEN = jwtUtil.createRefreshToken(savedUser.getId());
	}
}
